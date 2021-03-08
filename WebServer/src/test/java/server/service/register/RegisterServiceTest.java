package server.service.register;

import org.junit.Test;

import server.ServiceTestSuite;
import server.dao.DatabaseException;
import server.model.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Garret R Gang on 10/23/19.
 */
public class RegisterServiceTest extends ServiceTestSuite
{


    @Test
    public void register() throws Exception {
        RegisterRequest request = new RegisterRequest(ServiceTestSuite.fakeUser, ServiceTestSuite.dummy);
        RegisterResult result = new RegisterService(db).register(request);

        assertEquals(result.getUserName(), ServiceTestSuite.fakeUser.getUserName());

        db.open();
        User user = db.getUserDao().getUser( ServiceTestSuite.fakeUser.getUserName() );
        assertEquals(user , ServiceTestSuite.fakeUser) ;

        //this test is to make sure it is full
        assertNotEquals(db.getPersonDao().getPeople(user.getUserName()).length, 0 );

        //now try to register again... with same data. this should fail..
    }

    @Test
    public void badRegisteration() throws Exception {

        RegisterRequest request = new RegisterRequest(ServiceTestSuite.fakeUser, ServiceTestSuite.dummy);
        RegisterResult result = new RegisterService(db).register(request);
        db.open();
        User user = db.getUserDao().getUser( ServiceTestSuite.fakeUser.getUserName() );
        assertEquals(user , ServiceTestSuite.fakeUser) ;

        //this test is to make sure it is full
        assertNotEquals(db.getPersonDao().getPeople(user.getUserName()).length, 0 );

        boolean it_failed = false;
        try {
            RegisterResult fail = new RegisterService(db).register(request);
        }
        catch(DatabaseException e)
        {
            it_failed = true;
        }
        assertTrue(it_failed);
    }

}