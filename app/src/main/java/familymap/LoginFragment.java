package familymap;

import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import familymap.R;
import familymap.server.BadLoginException;
import familymap.server.Model;
import familymap.server.ServerError;
import familymap.server.ServerProxy;

import server.service.login.LoginRequest;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    protected static final String USER_NAME = "param1";
    protected static final String PASSWORD = "param2";

    // TODO: Rename and change types of parameters
    protected String mUserName;
    protected String mPassword;


    protected EditText userNameEdit;
    protected EditText passwordEdit;
    protected Button queryDatabaseButton;
    protected TextView switchStateButton;

    private OnFragmentInteractionListener mListener;

    public LoginFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userName user name to login.
     * @param password password already entereted by user.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String userName, String password)
    {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(USER_NAME, userName);
        args.putString(PASSWORD, password);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mUserName = getArguments().getString(USER_NAME);
            mPassword = getArguments().getString(PASSWORD);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_login, container, false);



        userNameEdit = (EditText) v.findViewById(R.id.userNameEdit);
        passwordEdit= (EditText) v.findViewById(R.id.passwordEdit);

        queryDatabaseButton = v.findViewById(R.id.login_button);
        queryDatabaseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                queryServer();
            }
        });

        if(mUserName == null || mUserName.equals(""))
        {
            queryDatabaseButton.setEnabled(false);
        }
        else
        {
            userNameEdit.setText(mUserName);
        }
        if(mPassword == null || mPassword.equals(""))
        {
            queryDatabaseButton.setEnabled(false);
        }
        else
        {
            passwordEdit.setText(mPassword);
        }



        TextWatcher mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (userNameEdit.getText().toString().equals("") || passwordEdit.getText().toString().equals(""))
                {
                    queryDatabaseButton.setEnabled(false);
                }
                else
                {
                    queryDatabaseButton.setEnabled(true);
                }
            }
        };
        userNameEdit.addTextChangedListener(mTextWatcher);
        passwordEdit.addTextChangedListener(mTextWatcher);

        switchStateButton = v.findViewById(R.id.login_SwitchStatement);
        switchStateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onSwitchButtonClicked();
            }
        });
        // Inflate the layout for this fragment
        return v;
    }


    public void queryServer()
    {
        //TODO login activity

        //first a basic check to see if we already have a port and ip address
        if(Model.instance().getServerHost() == null || Model.instance().getServerHost() == null )
        {
            ((FamilyMap) getActivity() ).createServerInfoQuery(this); //easy...
        }
        else
        {
            LoginRequest lr = new LoginRequest( getUserName(), getPassword() );
            WebAccess wa = new WebAccess();
            wa.execute(lr);
            deactivateButtons();
        }

    }

    private int m_b1_color;
    private int m_b1t_color;
    private int m_b2_color;
    protected void deactivateButtons()
    {
        //save data for later reactivation.
    //    m_b1_color = ((ColorDrawable)queryDatabaseButton.getBackground()).getColor();
        m_b2_color = switchStateButton.getCurrentTextColor();

        //disable clicking
        queryDatabaseButton.setEnabled(false);
        switchStateButton.setClickable(false);

        //make less clickable looking.
        //queryDatabaseButton.setBackgroundColor(getResources().getColor(R.color.inactiveButton) );
        switchStateButton.setTextColor(getResources().getColor(R.color.inactiveButton));
    }
    protected void reactivateButtons()
    {
        switchStateButton.setClickable(true);
        queryDatabaseButton.setEnabled(true);

        //queryDatabaseButton.setBackgroundColor(m_b1_color );
        switchStateButton.setTextColor(m_b2_color);
    }
    protected void onSwitchButtonClicked()
    {
        ( (FamilyMap)getActivity() ).toggle();
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    boolean noNullUserInput()
    {
        return !(getUserName().equals("") || getPassword().equals("") );
    }

    public String getUserName()
    {
        return userNameEdit.getText().toString();
    }

    public String getPassword()
    {
        return passwordEdit.getText().toString();
    }





    private enum ErrorCode
    {
        SUCCESS, LOGIN_ERROR, CONNECTION_ERROR
    }


    private class WebAccess extends AsyncTask<LoginRequest, Integer, ErrorCode>
    {
        protected ErrorCode doInBackground(LoginRequest...request)
        {

            ServerProxy server= new ServerProxy(Model.instance());
            try
            {
                server.login(request[0]);
            }
            catch(BadLoginException e)
            {
                return ErrorCode.LOGIN_ERROR;
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
                case LOGIN_ERROR:
                    Toast.makeText(main.getBaseContext(), "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECTION_ERROR:
                    //need to specify host.
                    //tell what went wrong? maybe in a later version..
                    //Toast.makeText(main.getBaseContext(), "Check portnumber and hostname", Toast.LENGTH_SHORT).show();
                    main.createServerInfoQuery(LoginFragment.this);
                    break;
            }

            reactivateButtons();
        }
    }
}

