package server.service.login;

import server.dao.Database;
import server.dao.DatabaseException;
import server.dao.UnauthorizedException;
import server.model.Authtoken;

/**
 *Service that handles login requests
 *<pre>
 *<b>Domain<b>
 * commRoute  : Database
 *</pre>
 */
public class LoginService
{
    Database db;
    /**
     *Only Constructer for the loginService
     *<pre>
     *<b>Constraints</b>
     * currentSession is not null.
     *</pre>
     *
     */
    LoginService() throws DatabaseException
    {
        db = new Database(Database.DEFAULT_DATABASE);
    }

    LoginService(Database db)
    {
        this.db = db;
    }
    /**
     *logs a user in.
     */
    LoginResult login(LoginRequest r) throws DatabaseException , UnauthorizedException
    {
        if(!db.isOpen()) db.open();
        try
        {
            Authtoken a = db.getAuthtokenDao().login(r.getUserName(), r.getPassword());
            db.close(true);

            return new LoginResult(a.getCode(), r.getUserName(), a.getUser().getMe().getPersonID());
        }
        catch(DatabaseException|UnauthorizedException e)
        {
            db.close(false);
            throw e;
        }

    }
}