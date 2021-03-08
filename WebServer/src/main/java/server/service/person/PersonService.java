package server.service.person;

import server.dao.Database;
import server.dao.DatabaseException;
import server.dao.UnauthorizedException;
import server.model.Person;

/**
 *A service for getting a single person by their id.
 *
 */
public class PersonService
{
    Database db;
    /**
     *This will channel its requests through s.
     */
    public PersonService(Database db)
    {
        this.db = db;

    }

    /**
     *Person getter
     */
    public Person getPerson(PersonRequest pr) throws UnauthorizedException, DatabaseException
    {
        Person output = null;
        try
        {
            if(!db.isOpen()) db.open();
            String user_id =db.getAuthtokenDao().validate( pr.getAuthority() );
            if( user_id ==null)
            {
                db.close(false);
                throw new UnauthorizedException(null, "error Invalid Authtoken");
            }
            output = db.getPersonDao().getPerson(pr.getId(), user_id);
            db.close(false);//no changes should occur
        }
        catch (DatabaseException e)
        {
            System.out.println("Threw Error" + e.getError());
            db.close(false);
            throw new DatabaseException(e.getError() );
        }
        return output;
    }
    /**
     *Gets all of the family members of a user
     */
    public Person[] getPeople(PeopleRequest pr) throws UnauthorizedException, DatabaseException
    {
        Person output[] = null;
        try
        {
            if(!db.isOpen()) db.open();
            String user_id =db.getAuthtokenDao().validate( pr.getAuthority() );
            if( user_id ==null)
            {
                db.close(false);
                throw new UnauthorizedException("fake user", "error use Invalid Authtoken");
            }
            output = db.getPersonDao().getPeople(user_id);
            db.close(false);
        }
        catch (DatabaseException e)
        {
            db.close(false);
            System.out.println("Threw exception");
            throw new DatabaseException(e.getMessage() );
        }
        return output;
    }
}