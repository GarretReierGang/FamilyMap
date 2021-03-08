package server.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class Database
{
    //public static final String DEFAULT_DATABASE = "database/fms_database.sqlite3";
    public static final String DEFAULT_DATABASE = "fms_database.sqlite3";
    public static final String DROP_PERSON = "drop table if exists PERSON";
    public static final String DROP_AUTH_TOKEN= "drop table if exists AUTH_TOKENS";
    public static final String DROP_EVENTS = "drop table if exists EVENTS";
    public static final String DROP_SECURITY = "drop table if exists SECURITY";
    public static final String DROP_USERS = "drop table if exists USERS";
    public static final String CREATE_USER_TABLE = "CREATE TABLE USERS\n" +
            " ( " +
                "`USER_NAME` TEXT NOT NULL UNIQUE, " +
                "`USER_ID` TEXT NOT NULL UNIQUE, `ME` TEXT, " +
                "`EMAIL` TEXT NOT NULL UNIQUE, " +
                "`PASSWORD` TEXT NOT NULL, " +
                "PRIMARY KEY(`USER_NAME`) " +
            ")";

    public static final String CREATE_PERSON_TABLE = "" +
            "CREATE TABLE PERSON \n" +
            "( " +
                "`PERSON_ID` TEXT NOT NULL UNIQUE, " +
                "`ASSOCIATED_USER` TEXT NOT NULL, " +
                "`FIRST_NAME` TEXT, " +
                "`LAST_NAME` TEXT, " +
                "`FATHER` TEXT, " +
                "`MOTHER` TEXT, " +
                "`SPOUSE` TEXT, " +
                "`GENDER` TEXT, " +
                "PRIMARY KEY(`PERSON_ID`), " +
                "FOREIGN KEY(`ASSOCIATED_USER`) REFERENCES `USERS`(`USER_NAME`) " +
            ")";

    public static final String CREATE_AUTHTOKEN_TABLE = "CREATE TABLE AUTH_TOKENS\n " +
            "( " +
                "`AUTH_ID` TEXT NOT NULL PRIMARY KEY UNIQUE, " +
                "`USER_NAME` TEXT NOT NULL, " +
                "FOREIGN KEY(`USER_NAME`) REFERENCES `USERS`(`USER_NAME`) " +
            ")";

    public static final String CREATE_EVENTS_TABLE = "CREATE TABLE EVENTS\n " +
            "(" +
                " `EVENT_ID` TEXT NOT NULL PRIMARY KEY UNIQUE, " +
                "`ASSOCIATED_USER` TEXT, " +
                "`LATITUDE` REAL NOT NULL, " +
                "`LONGITUDE` REAL NOT NULL, " +
                "`COUNTRY` TEXT, " +
                "`CITY` TEXT, " +
                "`EVENT_TYPE` TEXT, " +
                "`YEAR` INT NOT NULL," +
                " `PERSON_ID` TEXT, " +
                "FOREIGN KEY(`PERSON_ID`) REFERENCES `PERSON`(`PERSON_ID`), " +
                "FOREIGN KEY(`ASSOCIATED_USER`) REFERENCES `USERS`(`USER_NAME`) " +
            ")";

    public static final String CREATE_SECURITY_TABLE = "CREATE TABLE SECURITY\n " +
            "(" +
                " `USER_NAME` TEXT NOT NULL UNIQUE," +
                " `SALT` TEXT DEFAULT 'AxrEsQesD' UNIQUE " +
            ")";

    static {
        try {
            final String driver = "org.sqlite.JDBC";
            Class.forName(driver);
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    final String CONNECTION_URL = "jdbc:sqlite:";



    Connection dataConnection;
    PersonDao pDao;
    EventDao eDao;
    UserDao uDao;
    AuthtokenDao aDao;
    private boolean isOpen;
    String url;
    private final boolean debug;

    public static void displayError(SQLException e)
    {
        System.out.println(e.getMessage());
        System.out.println(e.getStackTrace());
    }

    /**
     * @param dataBaseName name of the database to connect to.
     */
    public Database(String dataBaseName) throws DatabaseException
    {
        isOpen = false;
        this.url = CONNECTION_URL + dataBaseName;
        dataConnection = null;

        pDao = new PersonDao(this);
        eDao = new EventDao(this);
        uDao = new UserDao(this);
        aDao = new AuthtokenDao(this);
        debug = false;
        this.open();
    }



    /**
     * @return will never be null. see PersonDao for usage
     */
    public PersonDao getPersonDao()
    {
        return pDao;
    }

    /**
     * @return will never be null. see EventDao for usage
     */
    public EventDao getEventDao()
    {
        return eDao;
    }

    /**
     * @return will never be null. see UserDao for usage
     */
    public UserDao getUserDao()
    {
        return uDao;
    }

    /**
     * @return will never be null. see AuthtokenDao for usage
     */
    public AuthtokenDao getAuthtokenDao()
    {
        return aDao;
    }

    /**
     * closes the transections and cleans up.
     * @param commit, true the changes become persistant, false they are not saved.
     */
    public void close(boolean commit)
    {
        this.isOpen = false;
        try
        {
            if(commit)
            {
                dataConnection.commit();
            }
            dataConnection.close();
            if (debug)
                System.out.println("closed a connection to the database.");
        }
        catch(SQLException except)
        {
            except.printStackTrace();
        }
    }

    /**
     * useful for recreating all of the tables. note this doesn't commit the changes.
     */
    public void createTables() throws DatabaseException
    {
        try
        {
            PreparedStatement users_stmnt = dataConnection.prepareStatement(CREATE_USER_TABLE);
            PreparedStatement persons_stmnt = dataConnection.prepareStatement(CREATE_PERSON_TABLE);
            PreparedStatement authtokens_stmnt = dataConnection.prepareStatement(CREATE_AUTHTOKEN_TABLE);
            PreparedStatement events_stmnt = dataConnection.prepareStatement(CREATE_EVENTS_TABLE);
            PreparedStatement security_stmnt = dataConnection.prepareStatement(CREATE_SECURITY_TABLE);
            users_stmnt.execute();
            persons_stmnt.execute();
            authtokens_stmnt.execute();
            events_stmnt.execute();
            security_stmnt.execute();


            users_stmnt.close();
            persons_stmnt.close();
            authtokens_stmnt.close();
            events_stmnt.close();
            security_stmnt.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
    }

    /**
     * destroys all the tables in the db. Note you must use close(true) for this to take effect.
     */
    public void destroyTables() throws DatabaseException
    {
        try {
            PreparedStatement stmnt = dataConnection.prepareStatement(DROP_PERSON);
            stmnt.execute();
            stmnt.close();
            stmnt = dataConnection.prepareStatement(DROP_EVENTS);
            stmnt.execute();
            stmnt.close();
            stmnt = dataConnection.prepareStatement(DROP_AUTH_TOKEN);
            stmnt.execute();
            stmnt.close();
            stmnt = dataConnection.prepareStatement(DROP_USERS);
            stmnt.execute();
            stmnt.close();
            stmnt = dataConnection.prepareStatement(DROP_SECURITY);
            stmnt.execute();
            stmnt.close();
        }
        catch(SQLException e)
        {
            Database.displayError(e);
            System.out.print(e.getStackTrace());
            throw new DatabaseException(e.getMessage());
        }
    }

    public void open() throws DatabaseException
    {
        if(this.isOpen) throw new DatabaseException("Tried to open an open line");
        this.isOpen = true;
        try {
            dataConnection = DriverManager.getConnection(url);
            dataConnection.setAutoCommit(false);
            if (debug)
                System.out.println("Created a new connection to the database.");
        }
        catch(SQLException except)
        {
            except.printStackTrace();
            throw new DatabaseException(except.getSQLState());
        }
    }

    public Connection getConnection()
    {
        return dataConnection;
    }

    public boolean isOpen()
    {
        return isOpen;
    }
}