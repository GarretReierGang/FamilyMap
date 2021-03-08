package familymap.branch.Map;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import familymap.R;
import familymap.branch.Person.PersonActivity;
import familymap.branch.Settings.Settings;
import familymap.server.Model;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import server.model.Event;
import server.model.Person;


/**
 * A simple {@link Fragment} subclass.
 */
public class FamilyMapFragment extends Fragment implements GoogleMap.OnMarkerClickListener

{

    static final float GENERATION_REDUCTION_RATE= .7f;
    private TextView textButton;
    private ImageView imageView;
    private MapView map;
    private GoogleMap gMap;
    private Marker lastClicked;
    private Model model = Model.instance();

    private LinkedList<Polyline> polyLines = new LinkedList<>();
    private Map<Event, Marker> markers = new HashMap<>();

    public FamilyMapFragment()
    {
        // Required empty public constructor
    }

    private void setMapType(int i)
    {
        if(gMap == null) return;
        switch(i)
        {
            case 0:
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case 1:
                gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case 2:
                gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
        }
    }
    @Override
    public void onStart()
    {
        super.onStart();
        //update everything.
        for(Map.Entry<Event, Marker> entry : markers.entrySet()  )
        {
            entry.getValue().setVisible(model.filter(entry.getKey()));
        }

        setMapType(Model.instance().getSettings().getMapType());
        for(Polyline p: polyLines)
        {
            //change color based off of settings...
            p.setColor(getColor( (LineTypes) p.getTag() ) );
        }
    }

    private void processEvent(String PersonID)
    {
        Settings settings = model.getSettings();
        Person p = model.getPerson(PersonID);
        for(Event e: model.getEventSet(PersonID))
        {
            Marker temp = gMap.addMarker(new MarkerOptions()
                    .title(e.getEventType())
                    .snippet(p.getFirstName() + " " + p.getLastName())
                    .position(new LatLng(e.getLatitude(), e.getLongitude()))
                    .icon(BitmapDescriptorFactory.defaultMarker(settings.getEventTypeColor(e.getEventType()))));

            temp.setTag(e);
            markers.put(e, temp);
        }
    }
    private void processPeople()
    {
        Model data = Model.instance();

        for( Person p : data.getPeople() )
        {
            processEvent(p.getPersonID());
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.i("FamilyMapFragment","created the map fragment");
        View v = inflater.inflate(R.layout.fragment_google_map, container, false);
        // Inflate the layout for this fragment
        textButton = v.findViewById(R.id.map_textButton);
        textButton.setClickable(false);
        textButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                onTextClickedListener();
            }
        });

        imageView = v.findViewById(R.id.map_picture);

        map= (MapView) v.findViewById(R.id.mapView);
        map.onCreate(savedInstanceState);

        map.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                gMap = mMap;
                gMap.setOnMarkerClickListener(FamilyMapFragment.this);
                gMap.getUiSettings().setMapToolbarEnabled(false);
                processPeople();

                //check for passed parameter
                Bundle b = getArguments();
                setMapType(Model.instance().getSettings().getMapType());
                if(b != null)
                {
                    String eventId = b.getString("eventId");
                    if (eventId != null)
                    {
                        //todo run the zoom
                        Event event = Model.instance().getEvent(eventId);
                        Marker marker = markers.get(event);
                        onMarkerClick(marker);
                        marker.showInfoWindow();
                        gMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 250, null);
                    }
                }
            }
        });


        return v;
    }

    public void onTextClickedListener()
    {
        PersonActivity.start(getActivity(), ((Event)lastClicked.getTag() ).getConnectedId());
    }


    private void destroyPolylines()
    {
        for(Polyline p : polyLines)
        {
            p.remove();
        }
        polyLines = new LinkedList<>();
    }

    @Override
    public boolean onMarkerClick(final Marker marker)
    {
        Log.i("FamilyMapFragment","onMarkerClick ran");
        if(marker.equals(lastClicked)) return false;
        lastClicked = marker;
        //destroy existing polylines.. It might be better to hide/unhide them.. but meh..

        destroyPolylines();


        Event root = (Event)marker.getTag();
        //process the event by loading in the bottom region..
        textButton.setClickable(true);//even if it already was...
        String text = String.format(getString(R.string.map_button_format), marker.getSnippet(), root.getEventType(), root.getPrettyLocation(), root.getDate() );
        textButton.setText(text);
        Model model = Model.instance();
        Person p = model.getPerson(root.getConnectedId());
        //TODO set image based on gender.
        if(p.isMale())
        {
            imageView.setImageResource(R.drawable.male);
        }
        else
        {
            imageView.setImageResource(R.drawable.female);
        }
        Settings settings = model.getSettings();

        //TODO generate lines...
        //generate life lines
        if(settings.getDisplayLifeLine()) drawEventLines(marker, root);

        //generate spouse lines
        if(settings.getDisplaySpousalLine()) drawSpouseLine(marker, p);//and spouse is enabled


        //TODo generate generational lines
        if(settings.getDisplayGenerationLine()) drawGenerationalLines(marker, p, 1.0f);

        return false;//we want the automatic camera focus.
    }

    private Marker getFirstMarker(Person p)
    {
        Event spouseFirstEvent = model.getEventSet(p.getPersonID()).first();
        return markers.get(spouseFirstEvent);
    }
    private void drawSpouseLine(Marker marker, Person p)
    {
        if(p.getSpouseId() == null) return;//don't throw an error just don't draw the lines.
        Person spouse = model.getPerson(p.getSpouseId() );
        Marker spouseMarker =  getFirstMarker(spouse);

        Event spouseFirstEvent = model.getEventSet(p.getPersonID()).first();
        addLine(marker, spouseMarker, LineTypes.SPOUSAL);
    }
    private void drawGenerationalLines(Marker marker, Person p, float scale)
    {
        //private recursive function

        if(p.getFatherId()!= null)
        {
            //draw line and call this function, using dads marker.
            Person father = model.getPerson(p.getFatherId());
            Marker fatherMarker = getFirstMarker(father);
            Polyline line = addLine(marker,fatherMarker,LineTypes.FAMILY);
            if (line != null) {
                float width = line.getWidth() * scale;
                line.setWidth(width);
                drawGenerationalLines(fatherMarker, father, scale * GENERATION_REDUCTION_RATE);
            }
        }
        if(p.getMotherId() != null)
        {
            Person mother = model.getPerson(p.getMotherId());
            Marker motherMarker = getFirstMarker(mother);
            Polyline line = addLine(marker,motherMarker,LineTypes.FAMILY);
            if (line != null) {
                float width = line.getWidth() * scale;
                line.setWidth(width);
                drawGenerationalLines(motherMarker, mother, scale * GENERATION_REDUCTION_RATE);
            }
        }
    }
    private void drawEventLines(Marker marker, Event root)
    {
        Set<Event> events = Model.instance().getEventSet(root.getConnectedId());

        Marker prev = null;
        for(Event e: events)
        {

            Marker cur = markers.get(e);
            if(prev != null && Model.instance().filter(e))
            {
                addLine(prev, cur, LineTypes.LIFETIME );
            }
            prev = cur;
        }
    }



    private Polyline addLine(Marker first, Marker second, LineTypes lt)
    {
        //TODO set color based on LineTypes.
        int color = getColor(lt);

        if (first != null && second != null) {
            Log.d("addLine", "adding a line");
            Polyline created = gMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(first.getPosition().latitude, first.getPosition().longitude))
                    .add(new LatLng(second.getPosition().latitude, second.getPosition().longitude))
                    .color(color));
            created.setTag(lt);


            polyLines.add(created);
            return created;
        }
        return null;
    }

    private int getColor( LineTypes lt)
    {
        int color = 0;
        Settings settings = model.getSettings();
        switch(lt)
        {
            case FAMILY:
                color = Settings.getColorInt(settings.getGenerationLineColor() );
                break;
            case SPOUSAL:
                color = Settings.getColorInt(settings.getSpousalLineColor() );
                break;
            case LIFETIME:
                color = Settings.getColorInt(settings.getLifeLineColor() );
                break;
        }
        return color;
    }



    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        map.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        map.onLowMemory();
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Google.
     */
    // TODO: create an overload that takes an id as an argument.
    public static FamilyMapFragment newInstance()
    {
        FamilyMapFragment fragment = new FamilyMapFragment();
        return fragment;
    }

    public static FamilyMapFragment newInstance(String eventId)
    {
        FamilyMapFragment fragment = new FamilyMapFragment();
        Bundle args = new Bundle();
        args.putString("eventId",eventId);
        fragment.setArguments(args);
        return fragment;
    }
}
