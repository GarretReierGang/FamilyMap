package server.service.clear;

import server.dao.Database;
import server.dao.DatabaseException;

/**
 * Clears the database
 *
 *<pre>
 *<b>Domain</b>
 * sesion	: Database //conection to the database
 *</pre>
 */
public class ClearService
{

    Database db;
    ClearService(Database db)
    {
        this.db = db;
    }
    /**
     *clears the database
     */
    public ClearResult clear()
    {
        try
        {
            if(!db.isOpen())db.open();
            db.destroyTables();
            db.createTables();
            db.close(true);
            return new ClearResult("Clear succeeded.");
        }
        catch(DatabaseException e)
        {
            if(db != null) db.close(false);
            return new ClearResult("Clear Failed " +e.getError());
        }
    }
}