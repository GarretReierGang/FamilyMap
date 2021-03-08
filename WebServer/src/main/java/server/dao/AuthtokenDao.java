package server.dao;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import server.dao.security.Password;
import server.model.Authtoken;
import server.model.User;


public class AuthtokenDao
{
    final String SALT_SQL = "select USER_NAME, SALT FROM SECURITY\n" +
            "where USER_NAME = ?";
    final String SALT_INSERT = "insert into SECURITY(USER_NAME, SALT) values(?,?)";
    final String INSERT_AUTHTOKEN = "insert into AUTH_TOKENS(USER_NAME, AUTH_ID)\n" +
            "VALUES(?,?)";

    final String AUTHORIZE = "select USER_NAME, AUTH_ID\n" +
            "from AUTH_TOKENS\n" +
            "where AUTH_ID = ?";

    Database boss;
    /**
     * @param boss Connection this will send data to and read data from.
     */
    public AuthtokenDao( Database boss)
    {

        this.boss = boss;
    }

    /**
     *Logins in a user, giving them an authtoken
     *
     * @param user_name --must be in the database
     * @param password --must be connected to the userName
     * @return null, if userName and password don't match.
     */
    public Authtoken login(String user_name, String password) throws DatabaseException, UnauthorizedException
    {
        Authtoken authorization = null;

        try
        {

            User usr = boss.getUserDao().getUser(user_name);
            String salt = this.getSalt( user_name );

            //check to see if the passwords match
            if(!Password.authenticate(password, usr.getPassword(), salt))
            {
                throw new UnauthorizedException( user_name, "used bad passwords");
            }

            //generate model objects

            authorization = new Authtoken(usr, UUID.randomUUID().toString());

            //now create the data into the database

            PreparedStatement write_auth = boss.getConnection().prepareStatement(INSERT_AUTHTOKEN);
            write_auth.setString(1,authorization.getUserName());
            write_auth.setString(2,authorization.getCode());

            write_auth.execute();
            write_auth.close();
        }
        catch(SQLException e)
        {
            Database.displayError(e);
            throw new DatabaseException(e.getMessage());
        }
        return authorization;
    }

    public String validate(String token) //throws DatabaseException
    {
        try
        {
            PreparedStatement stmnt = boss.getConnection().prepareStatement(AUTHORIZE);
            stmnt.setString(1,token);
            ResultSet results = stmnt.executeQuery();
            String temp = null;
            if(results.next() )//true if there is at least one matching token
            {
                temp = results.getString("USER_NAME");
            }
            return temp;
        }
        catch(SQLException e)
        {
            return null;//
            //throw new DatabaseException( e.getMessage() );
        }

    }

    public void writeSalt(String username, String salt) throws DatabaseException
    {
        try
        {
            PreparedStatement stmnt = boss.getConnection().prepareStatement(SALT_INSERT);
            stmnt.setString(1,username);
            stmnt.setString(2,salt);
            stmnt.execute();
            stmnt.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
    }
    public String getSalt(String username) throws DatabaseException
    {
        String output = null;
        try
        {
            PreparedStatement stmnt = boss.getConnection().prepareStatement(SALT_SQL);
            stmnt.setString(1,username);
            ResultSet rs = stmnt.executeQuery();
            rs.next();
            output = rs.getString("SALT");
            rs.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return output;
    }

}