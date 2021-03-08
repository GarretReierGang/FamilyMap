package server.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import server.model.Event;


public class EventDao
{
    final int EVENT_ID      = 1;
    final int ASSOCIATED_USER    = 2;
    final int LATITUDE      = 3;
    final int LONGITUDE     = 4;
    final int COUNTRY       = 5;
    final int CITY          = 6;
    final int EVENT_TYPE    = 7;
    final int YEAR          = 8;
    final int PERSON_ID     = 9;

    final String SQL_INSERT = "insert into EVENTS(EVENT_ID, ASSOCIATED_USER, LATITUDE, LONGITUDE, COUNTRY, CITY, EVENT_TYPE, YEAR, PERSON_ID)\n" +
            "values(?,?,?,?,?,?,?,?,?)";

    final String SELECT_ALL = "select EVENT_ID, ASSOCIATED_USER, LATITUDE, LONGITUDE, COUNTRY, CITY, EVENT_TYPE, YEAR, PERSON_ID\n" +
            "from EVENTS\n" +
            "where ASSOCIATED_USER = ? ";

    final String SELECT = "select EVENT_ID, ASSOCIATED_USER, LATITUDE, LONGITUDE, COUNTRY, CITY, EVENT_TYPE, YEAR, PERSON_ID\n" +
            "from EVENTS\n" +
            "where EVENT_ID = ? AND ASSOCIATED_USER = ?  ";

    Database boss;

    public final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
    /**
     *Constructor
     * @param db a none null connection to pipe information to
     */
    public EventDao(Database db)
    {

        this.boss = db;
    }


    /**
     * adds an event to the database
     * @param e the event to save
     */
    public void writeEvent( Event e) throws DatabaseException
    {
        try
        {
            PreparedStatement stmnt = boss.getConnection().prepareStatement(SQL_INSERT);
            stmnt.setString(EVENT_ID, e.getId() );
            stmnt.setString(ASSOCIATED_USER, e.getAssociatedUsername() );
            stmnt.setDouble(LATITUDE, e.getLatitude() );
            stmnt.setDouble(LONGITUDE, e.getLongitude() );
            stmnt.setString(EVENT_TYPE, e.getEventType() );
            stmnt.setString(COUNTRY, e.getCountry() );
            stmnt.setString(CITY, e.getCity() );
            stmnt.setInt(YEAR, e.getDate());
            stmnt.setString(PERSON_ID, e.getConnectedId());

            stmnt.execute();
            stmnt.close();
        }
        catch(SQLException except)
        {
            except.printStackTrace();
            throw new DatabaseException(except.getMessage());
        }
    }


    /**
     * Loads all of the events belonging to a user to from the database and returns it.
     * @param user_id
     *
     *  @return all events belonging to owner
     */
    public Event[] getEvents(String user_id) throws DatabaseException
    {
        Event output[] = null;
        try
        {
            PreparedStatement stmnt = boss.getConnection().prepareStatement(SELECT_ALL);
            stmnt.setString(1, user_id);

            ResultSet rs = stmnt.executeQuery();

            ArrayList<Event> results = new ArrayList();
            while(rs.next())
            {
                String personId = rs.getString(PERSON_ID);
                String EventId = rs.getString(EVENT_ID);
                String associatedUser = rs.getString(ASSOCIATED_USER);
                double longitude = rs.getDouble(LONGITUDE);
                double latitude = rs.getDouble(LATITUDE);
                String city = rs.getString(CITY);
                String country = rs.getString(COUNTRY);
                int year =  rs.getInt(YEAR);
                String type = rs.getString(EVENT_TYPE);
                Event e = new Event(EventId, associatedUser,personId,latitude,longitude,country,city,type,year);
//
//                Event e = new Event(rs.getString(PERSON_ID), user_id, rs.getString(EVENT_ID) );
//                e.setLongitude( rs.getString(LONGITUDE) );
//                e.setLatitude( rs.getString(LATITUDE) );
//                e.setCity(rs.getString(CITY));
//                e.setCountry(rs.getString(COUNTRY));
//                e.setDate( rs.getInt(YEAR) );
//                e.setEventType( rs.getString(EVENT_TYPE));
//                results.add(e);
                results.add(e);
            }
            System.out.println("Finished adding event");

            rs.close();
            stmnt.close();
            output = new Event[results.size()];

            for(int i =0; i < results.size(); ++i)
            {
               // System.out.println(results.get(i).getId());
                output[i] = results.get(i);
            }
        }
        catch(SQLException except)
        {
            except.printStackTrace();
            throw new DatabaseException(except.getMessage());
        }
        return output;
    }

    public Event getEvent(String event_id, String user_id) throws DatabaseException
    {
        Event output = null;
        try
        {
            PreparedStatement stmnt = boss.getConnection().prepareStatement(SELECT);
            stmnt.setString(1, event_id);
            stmnt.setString(2, user_id);

            ResultSet rs = stmnt.executeQuery();
            rs.next();

            output = new Event(rs.getString(PERSON_ID), rs.getString(ASSOCIATED_USER), rs.getString(EVENT_ID) );
            output.setLongitude( rs.getDouble(LONGITUDE) );
            output.setLatitude( rs.getDouble(LATITUDE) );
            output.setCity(rs.getString(CITY));
            output.setCountry(rs.getString(COUNTRY));
            output.setDate(rs.getInt(YEAR));
            output.setEventType( rs.getString(EVENT_TYPE));

        }
        catch(SQLException except)
        {
            except.printStackTrace();
            throw new DatabaseException("event not found");
        }
        return output;
    }

    public int writeEvents(Event events[]) throws DatabaseException
    {
        int count = 0;
        String temp;
        try
        {
            PreparedStatement stmnt = boss.getConnection().prepareStatement(SQL_INSERT);

            for(Event e : events)
            {
                temp = e.getEventType();
                stmnt.setString(EVENT_ID, e.getId() );
                stmnt.setString(ASSOCIATED_USER, e.getAssociatedUsername() );
                stmnt.setDouble(LATITUDE, e.getLatitude() );
                stmnt.setDouble(LONGITUDE, e.getLongitude() );
                stmnt.setString(EVENT_TYPE, e.getEventType() );
                stmnt.setString(COUNTRY, e.getCountry() );
                stmnt.setString(CITY, e.getCity() );
                stmnt.setInt(YEAR, e.getDate());
                stmnt.setString(PERSON_ID, e.getConnectedId());

                count += stmnt.executeUpdate();
            }
            stmnt.close();
        }
        catch(SQLException e)
        {
            throw new DatabaseException( e.getMessage() );
        }
        return count;
    }

    static final String  DELETE_BELONGING_TO_USER = "delete \n" +
            "from EVENTS \n" +
            "where ASSOCIATED_USER = ?";
    public int delete(String user_id)
    {
        try {
            PreparedStatement stmnt = boss.getConnection().prepareStatement(DELETE_BELONGING_TO_USER);
            stmnt.setString(1, user_id);
            return stmnt.executeUpdate();
        }
        catch(SQLException except)
        {
            return 0;
        }
    }

}