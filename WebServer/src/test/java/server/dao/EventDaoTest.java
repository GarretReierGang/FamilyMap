package server.dao;

import org.junit.After;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Arrays;

import server.model.Event;
import server.model.Person;
import server.model.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Garret R Gang on 10/26/19.
 */
public class EventDaoTest {
    Database s;
    User u;
    @Before
    public void setUp() throws Exception
    {
        s = new Database("database/fms_database.sqlite3");
        Person me = new Person("Garret", "Gang", 'm');
        u = new User("Garret R Gang", "fakemail@gmail.com","password", me );
    }

    @After
    public void tearDown() throws Exception
    {
        s.close(false);
    }

    @org.junit.Test
    public void getEvent() throws Exception
    {
        Event e = new Event(u.getMe().getPersonID(), u.getUserName() );
        System.out.printf("%s  %s \n", e.getId(), e.getCity());
        s.getUserDao().writeUser(u);
        //s.getPersonDao().writePerson(u.getMe());
        s.getEventDao().writeEvent(e);

        Event loaded = s.getEventDao().getEvent(e.getId(), u.getUserName());

        assertEquals(e, loaded);
    }

    @org.junit.Test
    public void getEvents() throws Exception
    {
        Event e = new Event( u.getUserName(), u .getMe().getPersonID() );
        ArrayList<Event> events = new ArrayList(
                Arrays.asList(
                                new Event( u.getUserName(), u.getMe().getPersonID() ).setCity("Moscow").setCountry("USA"),
                                new Event( u.getUserName(), u.getMe().getPersonID() ).setCity("LesterBournes"),
                                 new Event( u.getUserName(), u.getMe().getPersonID() ) ) );

        for(Event ev : events)
        {
            s.getEventDao().writeEvent(ev);
        }

        Event events2[] = s.getEventDao().getEvents( u.getUserName() ) ;

        for(int i = 0; i < events2.length; ++i)
        {
            System.out.println( events.indexOf( events2[i] ) );
            assertTrue(events.indexOf(events2[i]) > -1 );
        }

    }

}