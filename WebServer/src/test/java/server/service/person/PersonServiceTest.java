package server.service.person;

import org.junit.Test;

import server.ServiceTestSuite;
import server.dao.DatabaseException;
import server.dao.UnauthorizedException;
import server.model.Authtoken;
import server.model.Person;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Garret R Gang on 10/23/19.
 */
public class PersonServiceTest extends ServiceTestSuite
{
    //this test depends on load working.



    @Test
    public void getPerson() throws Exception
    {
        writeData();
        Authtoken auth = db.getAuthtokenDao().login(fakeUser.getUserName(),fakeUser.getPassword());
        db.close(true);
        for(Person p : generated_data.people)
        {
            assertEquals(p, new PersonService(db).getPerson(new PersonRequest(auth.getCode(), p.getPersonID())));
        }
    }

    @Test
    public void getNonExistantPerson() throws Exception
    {
        writeData();
        Authtoken auth = db.getAuthtokenDao().login(fakeUser.getUserName(),fakeUser.getPassword());
        db.close(true);
        boolean exception_occured = false;
        try
        {
            new PersonService(db).getPerson(new PersonRequest( auth.getCode(),"randomString" ));
         }
        catch(DatabaseException | UnauthorizedException e)
        {
            exception_occured = true;
        }
        assertTrue(exception_occured);
    }


    @Test
    public void getPeople() throws Exception
    {
        writeData();
        Authtoken auth = db.getAuthtokenDao().login(fakeUser.getUserName(),fakeUser.getPassword());
        Person people[] = new PersonService(db).getPeople(new PeopleRequest(auth.getCode()));
        assertArrayEquals(people, generated_data.people);
    }

    @Test
    public void getPeopleBadAuth() throws Exception
    {
        writeData();
        boolean unauthorizedException = false;
        try {
            PersonService service = new PersonService(db);
            service.getPeople(new PeopleRequest("this will not work"));
        } catch (UnauthorizedException UE)
        {
            unauthorizedException = true;
        }
        assertTrue(unauthorizedException);
    }


}