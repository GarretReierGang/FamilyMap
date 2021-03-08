package server.service.register;

import server.dao.Database;
import server.dao.DatabaseException;
import server.dao.UnauthorizedException;
import server.model.Authtoken;
import server.model.Person;
import server.model.User;
import server.service.fill.Filler;

/**
 *Service that handles register requests
 *<pre>
 *<b>Domain<b>
 * commRoute  : Database
 *</pre>
 */
public class RegisterService {
    Database db;
    boolean fill;

    /**
     * Only Constructer for the loginService
     * <pre>
     * <b>Constraints</b>
     * currentSession is not null.
     * </pre>
     * //@param db the commincation route for data.
     */
    public RegisterService(Database db) {
        this.db = db;
        this.fill = true;
    }

    public RegisterService(Database db, boolean fill)
    {
        this(db);
        this.fill = fill;
    }

    /**
     *logs a user in.
     */
    public RegisterResult register(RegisterRequest r) throws DatabaseException
    {
        RegisterResult RR = null;

        try {
            if (!db.isOpen()) db.open();
            Person p = new Person(r.getFirstName(), r.getLastName(), r.getGender().charAt(0));

            User u = new User(r.getUserName(), r.getEmail(), r.getPassword(), p);
            db.getUserDao().writeUser(u);
            Authtoken auth = null;
            try
            {
                auth = db.getAuthtokenDao().login(u.getUserName(), u.getPassword());
            }
            catch(UnauthorizedException e)
            {
                //this should never happen, if it does something affected the database between write user, and login.
                db.close(false);
                throw new DatabaseException("Unknown error, Could not access database");
            }

            p.setAssociatedUsername(auth.getUserName());

            RR = new RegisterResult(auth.getCode(), p.getPersonID(), r.getUserName());
            //generating "ancestory"
            Filler.FillData data = new Filler().createGenerations(p, Filler.DEFAULT_GENERATIONS);
            String userName = u.getUserName();
            db.getPersonDao().delete(u.getUserName()); //Filler is designed to create a person for the user, to not change test cases, this was not chagned
            db.getPersonDao().writePeople(data.people);
            db.getEventDao().writeEvents(data.events);


            db.close(true);
        }
        catch(DatabaseException e)
        {
            if(db != null) db.close(false);
            throw e;
        }
        return RR;
    }
}