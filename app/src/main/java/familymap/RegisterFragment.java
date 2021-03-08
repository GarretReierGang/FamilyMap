package familymap;


import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import familymap.R;
import familymap.server.Model;
import familymap.server.NameAlreadyClaimedException;
import familymap.server.ServerError;
import familymap.server.ServerProxy;

import server.service.register.RegisterRequest;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends LoginFragment
{

    private EditText emailEdit;
    private EditText passwordConfirmEdit;
    private EditText lastNameEdit;
    private EditText firstNameEdit;
    private RadioGroup radioSex;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View v = inflater.inflate(R.layout.fragment_register, container, false);

        userNameEdit = (EditText) v.findViewById(R.id.userNameEdit);
        passwordEdit = (EditText) v.findViewById(R.id.passwordEdit);
        emailEdit = (EditText) v.findViewById(R.id.emailEdit);
        lastNameEdit = (EditText) v.findViewById(R.id.lastNameEdit);
        firstNameEdit = (EditText) v.findViewById(R.id.firstNameEdit);

        passwordConfirmEdit = (EditText) v.findViewById(R.id.passwordConfirmEdit);



        queryDatabaseButton = v.findViewById(R.id.registerButton);
        queryDatabaseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                queryServer();
            }
        });
        queryDatabaseButton.setEnabled(false);

        switchStateButton = v.findViewById(R.id.register_SwitchStatement);
        switchStateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onSwitchButtonClicked();
            }
        });

        radioSex= (RadioGroup) v.findViewById(R.id.registerRadio);


        if(mUserName != null || !mUserName.equals(""))
            userNameEdit.setText(mUserName);

        if(mPassword != null || mPassword.equals(""))
            passwordEdit.setText(mPassword);




        TextWatcher mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!noNullUserInput() )
                {
                    queryDatabaseButton.setEnabled(false);
                }
                else
                {
                    queryDatabaseButton.setEnabled(true);
                }

//                if (!passwordConfirmEdit.getText().toString().equals(passwordConfirmEdit.getText().toString()))
//                {
//                    FamilyMap main = (FamilyMap) getActivity();
//                    queryDatabaseButton.setEnabled(false);
//                    Toast.makeText(main.getBaseContext(), "Passwords Do Not Match.", Toast.LENGTH_SHORT).show();
//                }
            }
        };
        userNameEdit.addTextChangedListener(mTextWatcher);
        passwordEdit.addTextChangedListener(mTextWatcher);
        passwordConfirmEdit.addTextChangedListener(mTextWatcher);
        emailEdit.addTextChangedListener(mTextWatcher);
        lastNameEdit.addTextChangedListener(mTextWatcher);
        firstNameEdit.addTextChangedListener(mTextWatcher);




        return v;
    }


    public static RegisterFragment newInstance(String userName, String password)
    {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(USER_NAME, userName);
        args.putString(PASSWORD, password);
        fragment.setArguments(args);
        return fragment;
    }



    public void queryServer()
    {
        //TODO make this register user
        //first establish a connection to the database..
        if(!getPassword().equals(getPasswordConfirm()))
        {
            Toast.makeText( (getActivity() ) .getBaseContext(), "Password Mismatch.", Toast.LENGTH_SHORT).show();
            passwordEdit.setText("");
            passwordConfirmEdit.setText("");
        }
        else if(!noNullUserInput())
        {
            //TODO highlight unfished fields.
            Toast.makeText( (getActivity() ) .getBaseContext(), "Finish filling out the data!", Toast.LENGTH_SHORT).show();
        }
        else if(Model.instance().getServerHost() == null || Model.instance().getServerHost() == null )
        {
            ((FamilyMap) getActivity() ).createServerInfoQuery(this); //easy...
        }
        else
        {
            //try it out. if it fails
            WebAccess wa = new WebAccess();
            RegisterRequest rr = new RegisterRequest(this.getUserName(),this.getPassword(),this.getEmail(), this.getFirstName(), this.getLastName(), this.getGender());
            wa.execute(rr);
            deactivateButtons();
        }
    }
    boolean noNullUserInput()
    {
        return super.noNullUserInput() &&
                !(getEmail().equals("") || getPasswordConfirm().equals("") || getLastName().equals("")
                        || getFirstName().equals("") || getGender().equals(""));
    }
//getters


    public String getEmail()
    {
        return emailEdit.getText().toString();
    }

    public String getPasswordConfirm()
    {
        return passwordConfirmEdit.getText().toString();
    }

    public String getLastName()
    {
        return lastNameEdit.getText().toString();
    }

    public String getFirstName()
    {
        return firstNameEdit.getText().toString();
    }

    public String getGender()
    {
        return Character.toString( ( (RadioButton)getView().findViewById(radioSex.getCheckedRadioButtonId() ))
                .getText().toString().toLowerCase().charAt(0) );
    }

    private enum ErrorCode
    {
        SUCCESS, REGISTER_ERROR, CONNECTION_ERROR
    }
    private class WebAccess extends AsyncTask<RegisterRequest, Integer, ErrorCode>
    {
        protected ErrorCode doInBackground(RegisterRequest...request)
        {

            ServerProxy server= new ServerProxy(Model.instance());
            try
            {
                server.register(request[0]);
            }
            catch(NameAlreadyClaimedException e)
            {
                return ErrorCode.REGISTER_ERROR;
            }
            catch(ServerError e)
            {
                return ErrorCode.CONNECTION_ERROR;
            }

            return ErrorCode.SUCCESS;
        }

        protected void onProgressUpdate(Integer... progress)
        {
            int percent = progress[0];

        }

        protected void onPostExecute(ErrorCode error)
        {
            FamilyMap main = (FamilyMap) getActivity();
            switch(error)
            {
                case SUCCESS:
                    Model data = Model.instance();
                    Toast.makeText(main.getBaseContext(),data.getUserPerson().getFirstName() + " " +  data.getUserPerson().getLastName(), Toast.LENGTH_SHORT).show();
                    //TODO switch fragments to a map fragment
                    reactivateButtons();
                    main.loadMap();
                    return;
                case REGISTER_ERROR:
                    Toast.makeText(main.getBaseContext(), "Username or Email already claimed.", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECTION_ERROR:
                    //need to specify host.
                    //tell what went wrong? maybe in a later version..
                    Toast.makeText(main.getBaseContext(), "Check portnumber and hostname" + Model.instance().getServerHost() + ":" + Model.instance().getServerPort(), Toast.LENGTH_SHORT).show();
                    main.createServerInfoQuery(RegisterFragment.this);
                    break;
            }

            reactivateButtons();
        }
    }
}