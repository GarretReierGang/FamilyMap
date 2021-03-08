package server.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import server.model.Person;
import server.model.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Garret R Gang on 10/26/19.
 */
public class PersonDaoTest {
    Database s;
    @Before
    public void setUp() throws Exception {
        s = new Database("database/fms_database.sqlite3");

    }

    @After
    public void tearDown() throws Exception {
        s.close(false);
    }

    /**
     * There is no test for write person, it will be tested along with getPerson and get People.
     * if it doesn't work those won't work either as there is an interdependancy between the two.
     *
     */



    @Test
    public void getPerson() throws Exception {

        Person me = new Person("Garret", "Gang", 'm');
        User u = new User("Garret R Gang", "fakemail@gmail.com","password", me );
        Person p = new Person( "Fred", "Austere", 'm', u.getUserName() );
        s.getPersonDao().writePerson(p);
        Person p2 = s.getPersonDao().getPerson(p.getPersonID(), u.getUserName());// "c6c52a76-4ae2-4459-a261-9adff2f712a0");

        assertEquals(p , p2);

    }

    @Test
    public void getPersonErrors() throws Exception
    {
        boolean caughtADatabaseException = false;
        try {
            s.getPersonDao().getPerson("notther", "fakeError");//this should throw an error.
        }
        catch(DatabaseException except)
        {
            caughtADatabaseException = true;
        }
        assertTrue(caughtADatabaseException);
    }
    @Test
    public void getPeople() throws Exception
    {
        Person me = new Person("Garret", "Gang", 'm');
        User u = new User("Garret R Gang", "fakemail@gmail.com","password", me );
        ArrayList<Person> people  =  new ArrayList(
                Arrays.asList(
                        new Person( "Fred", "Austere", 'm', u.getUserName()),
                        new Person("Audrey", "Hepborn", 'f', u.getUserName()),
                        new Person("Meyer", "Oscar", 'm', u.getUserName()),
                        new Person("Frenstance", "Owens", 'm', u.getUserName() )  ) );

        for(Person p : people)
        {
            s.getPersonDao().writePerson(p);
        }

        Person people2[] = s.getPersonDao().getPeople( u.getId() ) ;

        for(int i = 0; i < people2.length; ++i)
        {
            System.out.println( people.indexOf( people2[i] ) );
            assertTrue(people.indexOf(people2[i]) > -1 );
        }

    }


}