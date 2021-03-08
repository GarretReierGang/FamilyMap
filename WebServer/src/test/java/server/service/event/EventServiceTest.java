package server.service.event;

import org.junit.Test;

import server.ServiceTestSuite;
import server.model.Authtoken;
import server.model.Event;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by Garret R Gang on 10/23/19.
 */
public class EventServiceTest extends ServiceTestSuite {
    static final int EVENTS_TO_CHECK = 100;
    @Test
    public void getEvent() throws Exception
    {
        writeData();
        Authtoken auth = db.getAuthtokenDao().login(fakeUser.getUserName(),fakeUser.getPassword());
        db.close(true);
        for (int n = 0; n < EVENTS_TO_CHECK; ++n)
        {
            Event e = generated_data.events[n];
            assertEquals(e, new EventService(db).getEvent(new EventRequest(auth.getCode(), e.getId() )));
        }
        /*
        for(Event e : generated_data.events)
        {
            assertEquals(e, new EventService(db).getEvent(new EventRequest(auth.getCode(), e.getId() )));
        }*/
    }

    @Test
    public void getEvents() throws Exception
    {
        writeData();
        Authtoken auth = db.getAuthtokenDao().login(fakeUser.getUserName(), fakeUser.getPassword());
        db.close(true);
        Event events[] = new EventService(db).getEvents(new EventsRequest(auth.getCode()));
        assertArrayEquals(events, generated_data.events);
    }
}