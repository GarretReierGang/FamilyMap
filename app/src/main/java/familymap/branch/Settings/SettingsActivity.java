package familymap.branch.Settings;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.core.app.NavUtils;

import familymap.R;
import familymap.server.BadLoginException;
import familymap.server.BadSessionId;
import familymap.server.ServerProxy;

import java.util.List;

import familymap.server.Model;

public class SettingsActivity extends Activity implements AdapterView.OnItemSelectedListener
{

    final float MAX_SIZE = 250.0f;
    private Spinner spousalSpinner;
    private  Spinner generationalSpinner;
    private Spinner lifeLineSpinner;

    private Spinner mapTypeSpinner;
    private Switch spousalSwitch;
    private Switch generationalSwitch;
    private Switch lifeLineSwitch;

    private Button logout;
    private Button resync;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getActionBar().setHomeButtonEnabled(true);
        Settings settings = Model.instance().getSettings();
        spousalSpinner = (Spinner) findViewById(R.id.settings_spousal_spinner);
        generationalSpinner = (Spinner) findViewById(R.id.settings_generation_spinner);
        lifeLineSpinner = (Spinner) findViewById(R.id.settings_lifeline_spinner);


        spousalSpinner.setOnItemSelectedListener(this);
        generationalSpinner.setOnItemSelectedListener(this);
        lifeLineSpinner.setOnItemSelectedListener(this);


        ArrayAdapter<String> adapter;
        List<String> list = Settings.getColorNames();
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spousalSpinner.setAdapter(adapter);
        generationalSpinner.setAdapter(adapter);
        lifeLineSpinner.setAdapter(adapter);

        spousalSpinner.setSelection(settings.getSpousalLineColor());
        generationalSpinner.setSelection(settings.getGenerationLineColor());
        lifeLineSpinner.setSelection(settings.getLifeLineColor());

        spousalSwitch = findViewById(R.id.settings_spousal_switch);
        generationalSwitch= findViewById(R.id.settings_generation_switch);
        lifeLineSwitch = findViewById(R.id.settings_lifeline_switch);

        spousalSwitch.setChecked(settings.getDisplaySpousalLine());
        generationalSwitch.setChecked(settings.getDisplayGenerationLine());
        lifeLineSwitch.setChecked(settings.getDisplayLifeLine());

        spousalSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    Model.instance().getSettings().setDisplaySpousalLine(isChecked);
                }
            });

        lifeLineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
             {
                 @Override
                 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                 {
                     Model.instance().getSettings().setDisplayLifeLine(isChecked);
                 }
             });

        generationalSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
             {
                 @Override
                 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                 {
                     Model.instance().getSettings().setDisplayGenerationLine(isChecked);
                 }
             });

        logout = findViewById(R.id.settings_logout);
        logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Model.instance().setUserPerson(null);
                NavUtils.navigateUpFromSameTask(SettingsActivity.this);
            }
        });

        resync = findViewById(R.id.settings_resync);
        resync.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new WebAccess().execute();
                NavUtils.navigateUpFromSameTask(SettingsActivity.this);
                //SettingsActivity.this.onOptionsItemSelected((MenuItem) findViewById(android.R.id.home ) );
                //NavUtils.navigateUpFromSameTask(SettingsActivity.this);
            }
        });


        mapTypeSpinner = findViewById(R.id.settings_map_spinner);

        ArrayAdapter<CharSequence> adapt = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.map_modes, android.R.layout.simple_spinner_item);

        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mapTypeSpinner.setAdapter(adapt);
        mapTypeSpinner.setSelection(settings.getMapType());
        mapTypeSpinner.setOnItemSelectedListener(this);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //TODO overwrite this..
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
    {
        int spinner = parent.getId();
        //fixed list of colors...
        Toast.makeText(getBaseContext(), "spinner", Toast.LENGTH_SHORT);
        switch (spinner)
        {
            case R.id.settings_spousal_spinner:
                Model.instance().getSettings().setSpousalLineColor(pos);
                break;
            case R.id.settings_generation_spinner:
                // your stuff here
                Model.instance().getSettings().setGenerationLineColor(pos);
                break;
            case R.id.settings_lifeline_spinner:
                Model.instance().getSettings().setLifeLineColor(pos);
                break;
            case R.id.settings_map_spinner:
                Toast.makeText(getBaseContext(), "map Spinner", Toast.LENGTH_SHORT);
                Model.instance().getSettings().setMapType(pos);
                break;
            }
        }
    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
        // Another interface callback
        //what does this do...
    }
    /*-----------Async Task-----------*/


    private enum ErrorCode
    {
        SUCCESS, CONNECTION_ERROR
    }

    private class WebAccess extends AsyncTask<Integer, Integer, ErrorCode>
    {
        protected SettingsActivity.ErrorCode doInBackground(Integer...request)
        {
            ServerProxy proxy = new ServerProxy(Model.instance());
            try
            {
                proxy.refresh();
            }
            catch(BadSessionId e)
            {
                //Toast.makeText(SettingsActivity.this, "Could not resync", Toast.LENGTH_SHORT);
                return ErrorCode.CONNECTION_ERROR;
            } catch (BadLoginException e) {
                return ErrorCode.CONNECTION_ERROR;
            }
            return SettingsActivity.ErrorCode.SUCCESS;
        }

        protected void onProgressUpdate(Integer... progress)
        {
            //do nothing..
        }

        protected void onPostExecute(SettingsActivity.ErrorCode error)
        {
            switch(error)
            {
                case SUCCESS:
                    Toast.makeText( SettingsActivity.this, "Synced data...", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECTION_ERROR:
                    Toast.makeText( SettingsActivity.this, "Could not connect to database", Toast.LENGTH_SHORT).show();
            }

        }
    }

}
