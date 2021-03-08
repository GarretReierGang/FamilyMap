package server.service.event;

import server.dao.Database;
import server.dao.DatabaseException;
import server.model.Event;

/**
 *A service for getting a single person by their id.
 *
 */
public class EventService
{
    Database db;
    /**
     *This will channel its requests through s.
     */
    public EventService(Database sess)
    {
        db = sess;
    }

    /**
     *Person getter
     */
    public Event getEvent(EventRequest er) throws DatabaseException
    {
        //db = new Database(Database.DEFAULT_DATABASE);
        try {
            System.out.println("Event: " + er.getEventId() + " " + er.getAuthority());

            if (!db.isOpen()) db.open();
            String user_id = db.getAuthtokenDao().validate(er.getAuthority());
            if (user_id == null) {
                db.close(false);
                System.out.println("Threw exception Invalid Authtoken");
                throw new DatabaseException("Invalid Authtoken");
            }
            Event e = db.getEventDao().getEvent(er.getEventId(), user_id);

            db.close(false);

            return e;
        }
        catch (DatabaseException de )
        {
            db.close(false);
            throw de;
        }
    }

    public Event[] getEvents(EventsRequest er) throws DatabaseException
    {
        Event events[];
        try {
            if (!db.isOpen()) db.open();
            String user_id = db.getAuthtokenDao().validate(er.getAuthority());
            if (user_id == null) {
                db.close(false);
                throw new DatabaseException("Invalid Authtoken");
            }
            events = db.getEventDao().getEvents(user_id);
            db.close(false);

            db.close(false);
        }
        catch (DatabaseException de)
        {
            db.close(false);
            throw de;
        }
        return events;
    }
}