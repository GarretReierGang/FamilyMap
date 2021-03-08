package server.service.clear;

import org.junit.Test;

import server.ServiceTestSuite;
import server.dao.DatabaseException;
import server.dao.UnauthorizedException;
import server.model.Authtoken;
import server.model.Person;
import server.service.person.PersonRequest;
import server.service.person.PersonService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Garret R Gang on 10/24/19.
 */
public class ClearServiceTest extends ServiceTestSuite
{
    @Test
    public void clear() throws Exception
    {
        writeData();
        System.out.println("Data Generated");
        //Database is not empty
        Authtoken auth = db.getAuthtokenDao().login(fakeUser.getUserName(),fakeUser.getPassword());
        db.close(true);
        System.out.println("Testing Write Success");
        for(Person p : generated_data.people)
        {
            assertEquals(p, new PersonService(db).getPerson(new PersonRequest(auth.getCode(), p.getPersonID())));
        }
        ClearService clearService = new ClearService(db);

        boolean noError = true;
        try {
            clearService.clear();
        }
        catch (Exception e)
        {
            noError = false;
        }
        assertTrue(noError);
        boolean loginFailed = false;
        try {
            auth = db.getAuthtokenDao().login(fakeUser.getUserName(), fakeUser.getPassword());
        } catch (DatabaseException e) {
            loginFailed = true;
        }
        assertTrue(loginFailed);
    }

}