package server;

import org.junit.After;
import org.junit.Before;

import server.dao.Database;
import server.model.Person;
import server.model.User;
import server.service.fill.Filler;

/**
 * Created by Garret R Gang on 10/23/19.
 */

public class ServiceTestSuite {
    static public Person dummy;
    static public User fakeUser;
    static public Filler.FillData generated_data;
    final static public int generation_count = 4;
    static{
        dummy = new Person("Im not real", "Fake data", 'q', "cake_lie", "my_id_is_forced");
        fakeUser = new User("cake_lie","fake@anonmail@aol.com", "secret_password", "my_id_is_forced", dummy);
        generated_data = new Filler().createGenerations(dummy, 10 );
    }



    protected Database db;
    @Before
    public void setUp() throws Exception {
        //db = new Database("TestSuite.sqlite3");
        System.out.println("Working Directory: " + System.getProperty("user.dir"));
        db = new Database(Database.DEFAULT_DATABASE);
        db.destroyTables();
        db.createTables();
    }

    @After
    public void tearDown() throws Exception {
        if(!db.isOpen()) db.open();
        db.destroyTables();
        db.createTables();
        db.close(true);
    }

    public void writeData() throws Exception
    {

        User user[] = new User[1];
        user[0] = fakeUser;
        db.getUserDao().writeUsers(user);
        db.getPersonDao().writePeople(generated_data.people);
        db.getEventDao().writeEvents(generated_data.events);
    }
}
