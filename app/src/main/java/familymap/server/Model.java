package familymap.server;

import android.util.Log;

import familymap.branch.Filter.EventTypeFilter;
import familymap.branch.Filter.FatherFilter;
import familymap.branch.Filter.Filter;
import familymap.branch.Filter.GenderFilter;
import familymap.branch.Filter.MotherFilter;
import familymap.branch.Settings.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import server.model.Event;
import server.model.Person;

/**
 * Created by Garret R Gang on 11/18/19.
 */

public class Model
{
    private String serverHost;
    private String serverPort;
    private String sessionToken;
    private String userId;
    private Person[] people;
    private Event[] events;

    private Map<String, Person> personMap;
    private Map<String, SortedSet<Event>> m_events;
    private Map<String, Person> personFemale;
    private Map<String, Person> personMale;

    private Map<String, Person> fathersSide;
    private Map<String, Person> mothersSide;

    private Map<String, List<String>> directRelations;
    private Map<String, Event> eventsMap;
    private ArrayList<Filter> filters;
    private Person user;

    private Settings settings;

    private Model()
    {
       clear();//this seems counter intuitive but, it will initialize all the data
    }

    public void clear()
    {
        personMap = new HashMap<>();
        m_events = new HashMap<>();
        personFemale = new HashMap<>();
        personMale = new HashMap<>();
        directRelations = new HashMap<>();
        settings = new Settings();
        eventsMap = new HashMap<>();
        filters = new ArrayList<>();
        fathersSide = new HashMap<>();
        mothersSide = new HashMap<>();

        addFilter(new MotherFilter());
        addFilter(new FatherFilter());
        addFilter(new GenderFilter('m'));
        addFilter(new GenderFilter('f'));
    }

    private static Model singleInstance;
    public static Model instance()
    {
        if(singleInstance == null)
        {
            singleInstance = new Model();
        }
        return singleInstance;
    }

    public Person[] getPeople()
    {
        ArrayList<Person> myPeople = new ArrayList<>();
        for (Person p : people)
        {
            if (this.filter(p))
            {
                myPeople.add(p);
            }
        }
        Person[] p = new Person[]{};
        return myPeople.toArray(p);
    }

    private void addDirectRelation(String personID, String relativeID)
    {
        List<String> list = directRelations.get(personID) ;
        if(list == null )
        {
            list = new LinkedList<String>();
            directRelations.put(personID, list );
        }
        list.add(relativeID);
    }
    public void setPeople(Person[] people)
    {
        for(Person p : people)
        {
            personMap.put(p.getPersonID(),p);
            if(p.isMale())
            {
                personMale.put(p.getPersonID(), p);
            }
            else
            {
                personFemale.put(p.getPersonID(), p);
            }
            if(p.getSpouseId() !=null) addDirectRelation(p.getSpouseId(), p.getPersonID());
            if(p.getFatherId() != null)
            {
                addDirectRelation(p.getFatherId(), p.getPersonID());
                addDirectRelation(p.getPersonID(), p.getFatherId());
            }
            if(p.getMotherId() != null)
            {
                Log.i(p.getFirstName(), p.getMotherId());
                addDirectRelation(p.getMotherId(), p.getPersonID() );
                addDirectRelation(p.getPersonID(), p.getMotherId() );
            }
        }
        this.people = people;
    }

    public Person getPerson(String personId)
    {
        return personMap.get(personId);
    }
    public Event[] getEvents()
    {
        ArrayList<Event> myEvents = new ArrayList<>();
        for (Event e : events)
        {
            if (this.filter(e))
            {
                myEvents.add(e);
            }
        }
        Event[] e = new Event[] {};
        return myEvents.toArray(e);
    }

    private void addEvent(Event e)
    {
        SortedSet<Event> set = m_events.get(e.getConnectedId());
        if(set == null)
        {
            set = new TreeSet<>();
            m_events.put(e.getConnectedId(),set);

        }
        set.add(e);
        eventsMap.put(e.getId(),e);
        if(settings.addEventType( e.getEventType() ) )
        {
            //todo addd the filter to filter array.
            filters.add(new EventTypeFilter(e.getEventType()));
        }

    }
    public void setEvents(Event[] events)
    {
        this.events = events;
        for(Event e: events)
        {
//            Log.d("model.setEvents",e.getEventType() +" "+ Integer.toString(e.getDate() ));
            addEvent(e);
        }
    }

    public String getServerHost()
    {
        return serverHost;
    }

    public void setServerHost(String serverHost)
    {
        this.serverHost = serverHost;
    }

    public String getServerPort()
    {
        return serverPort;
    }

    public void setServerPort(String serverPort)
    {
        this.serverPort = serverPort;
    }
    public void setUserPerson(String personId)
    {
        //find the person
        user = getPerson(personId);
        userId = personId;
    }

    public Person getUserPerson()
    {
        return user;
    }


    public Map<String, SortedSet<Event>> getEventSets ()
    {
        return m_events;
    }

    public SortedSet<Event> getEventSet(String personID)
    {
        return m_events.get(personID);
    }
    public Map<String, Person> getPersonMap()
    {
        return personMap;
    }
    public List<String> getDirectRelations(String personID)
    {
        return directRelations.get(personID);
    }

    public List<Person> getDirectRelationsAsPersonList(String personID)
    {
        List<Person> directFamily = new ArrayList<>();
        for(String relativeID: directRelations.get(personID))
        {
            Person p = getPerson(relativeID);
            if(p != null) directFamily.add(p);
        }
        return directFamily;
    }
    public Map<String, Person> getFemales()
    {
        return personFemale;
    }

    public Map<String, Person> getMales()
    {
        return personMale;
    }

    public Event getEvent(String eventId)
    {
        return eventsMap.get(eventId);
    }

    public String getSessionToken()
    {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken)
    {
        this.sessionToken = sessionToken;
    }

    public Settings getSettings()
    {
        return settings;
    }


    public void addFilter(Filter f)
    {
        filters.add(f);
    }

    public boolean filter(Event e)
    {
        for(Filter f: filters)
        {
            if(f.fails(e))
            {
                return false;
            }
        }
        return true;
    }
    public boolean filter(Person p)
    {
        for (Filter f: filters)
        {
            if (f.fails(p))
            {
                return false;
            }
        }
        return true;
    }


    public ArrayList<Filter> getFilters()
    {
        return filters;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }
}
