package server.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import server.dao.security.Password;
import server.model.Person;
import server.model.User;


public class UserDao
{
    final String REGISTER = "insert into USERS(USER_NAME, USER_ID, ME, EMAIL, PASSWORD)\n" +
            "values(?,?,?,?,?)";

    final String USER = "select USER_NAME, USER_ID, ME, EMAIL, PASSWORD \n" +
            "from USERS \n" +
            "where USER_NAME = ?";



    Database boss;
    /**
     * @param sess must
     */
    public UserDao(Database sess)
    {
        this.boss = sess;
    }
    /**
     * @param  u user to save to db.
     */
    public void writeUser(User u) throws DatabaseException
    {
        try {

            {
                String salt = Password.generateSalt();
                String hashed_password = Password.hash(u.getPassword(), salt);

                PreparedStatement stmnt = boss.getConnection().prepareStatement(REGISTER);

                stmnt.setString(1,u.getUserName());
                stmnt.setString(2,u.getId() );
                stmnt.setString(3,u.getMyPersonId());
                stmnt.setString(4,u.getEmail());
                stmnt.setString(5,hashed_password);
                stmnt.executeUpdate();
                stmnt.close();

                boss.getAuthtokenDao().writeSalt(u.getUserName(), salt);
                boss.getPersonDao().writePerson(u.getMe());
                //boss.getPersonDao().writePerson(u.getMe());
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
    }

    public int writeUsers(User users[]) throws DatabaseException
    {
        int count = 0;
        try {

            for(User u : users)
            {
                String salt = Password.generateSalt();
                String hashed_password = Password.hash(u.getPassword(), salt);

                PreparedStatement stmnt = boss.getConnection().prepareStatement(REGISTER);

                stmnt.setString(1,u.getUserName());
                stmnt.setString(2,u.getId() );
                stmnt.setString(3,u.getMyPersonId());
                stmnt.setString(4,u.getEmail());
                stmnt.setString(5,hashed_password);

                count += stmnt.executeUpdate();
                stmnt.close();

                boss.getAuthtokenDao().writeSalt(u.getUserName(), salt);
                //boss.getPersonDao().writePerson(u.getMe());
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return count;
    }
    public User getUser(String user_name) throws DatabaseException
    {
        User output = null;
        try
        {
            PreparedStatement ps = boss.getConnection().prepareStatement(USER);
            ps.setString(1, user_name);
            ResultSet user = ps.executeQuery();
            user.next();

            String user_id = user.getString("USER_ID");
            String email = user.getString("EMAIL");
            String password = user.getString("PASSWORD");
            String me_id = user.getString("ME");

            user.close();
            Person p = boss.getPersonDao().getPerson(me_id, user_name);

            output = new User(user_name, email, password, user_name, p );

        }
        catch(SQLException except)
        {
            throw new DatabaseException(except.getMessage() );
        }
        return output;
    }

    /**
     * creates a temporary password for the user and emails it to them.
     *
     * @param UserName user to reset
     * @param Email of the user, if it doesn't match the one on file this will throw an exception
     *
     * @return true if it was able to email the user.
     */
    public boolean resetPassword(String UserName, String Email) throws DatabaseException
    {
        return false;
    }
}