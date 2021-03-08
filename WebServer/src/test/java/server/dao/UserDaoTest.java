package server.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.model.Person;
import server.model.User;

import static org.junit.Assert.assertEquals;

/**
 * Created by Garret R Gang on 10/26/19.
 */
public class UserDaoTest {
    //this covers Authtoken checks as well.
    @Test
    public void writeUser() throws Exception
    {
        Person p = new Person("Garret", "Gang", 'm');
        User u = new User("Garret R Gang", "Garret R Gang@fakemail.net", "password" ,p);

        //Person(String firstName, String lastName, char sex)
        s.getUserDao().writeUser(u);

        User u2 = s.getUserDao().getUser(u.getUserName());

        assertEquals(u, u2);
    }

    @Test
    public void badUserRequest() throws Exception
    {

        Person p = new Person("Garret", "Gang", 'm');
        User u = new User("Garret R Gang", "Garret R Gang@fakemail.net", "password" ,p);

        //Person(String firstName, String lastName, char sex)
        s.getUserDao().writeUser(u);
        User u2 = null;
        try {
            u2 = s.getUserDao().getUser("bob");
        }
        catch (DatabaseException de)
        {
            assert ( de.getError().contains("ResultSet closed"));
        }
        assertEquals(u2, null);
    }

    Database s;
    @Before
    public void setUp() throws Exception {
        s = new Database("database/fms_database.sqlite3");

    }

    @After
    public void tearDown() throws Exception {
        s.close(false);
    }


}