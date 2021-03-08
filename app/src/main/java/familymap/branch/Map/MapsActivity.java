package familymap.branch.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import familymap.R;

/**
 * Created by Garret R Gang on 11/18/19.
 */
public class MapsActivity extends FragmentActivity
{

    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        FragmentManager fm = this.getSupportFragmentManager();
        currentFragment= fm.findFragmentById(R.id.currentFragmentFrame);

        if(currentFragment == null)
        {
            Bundle b = getIntent().getExtras();
            String eventId = b.getString("eventId");
            Log.i("MapActivityStart", eventId + "!!");
            currentFragment = FamilyMapFragment.newInstance(eventId);
            fm.beginTransaction()
                    .add(R.id.currentFragmentFrame, currentFragment)
                    .commit();
        }
    }

    public static void start(Context a, String eventId)
    {
        Intent intent = new Intent(a, MapsActivity.class);
        intent.putExtra("eventId", eventId);
        a.startActivity(intent);
    }

}
