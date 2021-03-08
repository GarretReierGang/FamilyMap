package server.service.load;

import server.dao.Database;
import server.dao.DatabaseException;
import server.model.Event;
import server.model.Person;

/**
 *A class responsible for loading large amounts of data to the Server.
 *
 */
public class LoadService
{
    Database db;
    /**
     *Constructer
     *<pre>
     *<b>Constraints</b>
     *  s must not be Null.
     */
    LoadService(Database db)
    {
        this.db = db;
    }

    /**
     *Fills the database with information and
     *@return a Success/Failure message
     */
    public LoadResult load(LoadRequest lr) throws DatabaseException
    {

        LoadResult load_result = null;
        try {
            if(!db.isOpen())db.open();

            db.destroyTables();
            db.createTables();

            if(lr.hasNullUsers())
            {
                db.close(false);
                throw new DatabaseException("Bad data in request");
            }
            int users = db.getUserDao().writeUsers(lr.getUsers());

            int people = db.getPersonDao().writePeople(lr.getPeople());

            int events = db.getEventDao().writeEvents(lr.getEvents());


            load_result = new LoadResult(users, people, events);
            db.close(true);
        }
        catch(DatabaseException e)
        {
            e.printStackTrace();
            load_result = new LoadResult(e.getError());
            if(db != null) db.close(false);
        }
        return load_result;
    }
    //check for nullity

    boolean hasIllegaNulls(LoadRequest lr)
    {
        for(Person p : lr.getPeople())
        {
            if(p.hasIllegalNulls()) return true;
        }

        for(Event e : lr.getEvents())
        {
            if(e.hasIllegalNulls()) return true;
        }

        for(Person p : lr.getPeople())
        {
            if(p.hasIllegalNulls()) return true;
        }

        return lr.hasNullUsers();
    }
}