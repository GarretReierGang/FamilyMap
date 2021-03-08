package familymap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import familymap.R;
import familymap.branch.Filter.FilterActivity;
import familymap.branch.Map.FamilyMapFragment;
import familymap.branch.Search.SearchActivity;
import familymap.branch.Settings.SettingsActivity;

import familymap.server.Model;


public class FamilyMap extends FragmentActivity
{

    private Fragment currentFragment;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("FamilyMap on Create","This thing");
        Model data = Model.instance();

        if(data.getServerHost() ==null || data.getServerPort() == null)
        {
            //TODO load host name and port from file.
            data.setServerPort("8000");
            data.setServerHost("10.0.2.2");
        }

        FragmentManager fm = this.getSupportFragmentManager();
        currentFragment= fm.findFragmentById(R.id.currentFragmentFrame);;
        if(currentFragment==null)
        {

            if(data.getUserPerson() == null)
            {
                Log.i("FamilyMap OnCreate", "recreateing currentFragment.");
                currentFragment = LoginFragment.newInstance(null, null);
                getActionBar().hide();
            }
            else
            {
                getActionBar().show();
                currentFragment = FamilyMapFragment.newInstance();
            }
            fm.beginTransaction()
                    .add(R.id.currentFragmentFrame, currentFragment)
                    .commit();
        }

    }

    private void updateFragment(Fragment frag)
    {
        currentFragment = frag;
        FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.currentFragmentFrame , currentFragment);
        ft.commit();
    }
    public void toggle()
    {

        if(currentFragment.getClass().equals(RegisterFragment.class))
        {
            updateFragment(LoginFragment.newInstance(
                    ((RegisterFragment) currentFragment ).getUserName(), ((RegisterFragment) currentFragment ).getPassword()));
        }
        else if( currentFragment.getClass().equals(LoginFragment.class) )
        {
            updateFragment(RegisterFragment.newInstance(
                    ((LoginFragment) currentFragment ).getUserName(), ((LoginFragment) currentFragment ).getPassword()) );
        }
        else
        {
            //does nothing if in the third state which is state map.
            return;
        }
    }

    public void loadMap()
    {
        //do nothing for now..
        //this.menu.setGroupVisible();
        getActionBar().show();
        updateFragment(FamilyMapFragment.newInstance());
    }



    public void createServerInfoQuery(LoginFragment callee)
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.popup_window, null);
        final EditText serverIp = alertLayout.findViewById(R.id.pop_serverIp);
        final EditText serverPort = alertLayout.findViewById(R.id.pop_serverPort);
        final LoginFragment frag = callee;
        serverIp.setText(Model.instance().getServerHost());
        serverPort.setText(Model.instance().getServerPort());


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Server Info");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Model data = Model.instance();
                data.setServerHost(serverIp.getText().toString() ) ;
                data.setServerPort( serverPort.getText().toString()) ;
                //now try to contact the server again..
                frag.queryServer();

            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.filter:
                //do filternewGame();
                FilterActivity.start(this);
                return true;
            case R.id.settings:
                //Toast.makeText(getBaseContext(), "settings", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.search:
                //Toast.makeText(getBaseContext(), "search", Toast.LENGTH_SHORT).show();
                SearchActivity.start(this);
                //todo search
                return true;
//            case R.id.back:
//                updateFragment(LoginFragment.newInstance(null, null));
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true; //set to false to have menu disabed by default.
    }
}
