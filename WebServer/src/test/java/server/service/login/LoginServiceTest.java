package server.service.login;

import org.junit.Test;

import server.ServiceTestSuite;
import server.dao.DatabaseException;
import server.dao.UnauthorizedException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by Garret R Gang on 10/23/19.
 */
public class LoginServiceTest extends ServiceTestSuite
{
    @Test
    public void login() throws Exception
    {
        db.getUserDao().writeUser(fakeUser);

        LoginResult result = null;
        LoginRequest request = new LoginRequest(fakeUser);
        try
        {
            result = new LoginService(db).login(request);
        }
        catch(UnauthorizedException e)
        {
            result = null;
        }

        catch(DatabaseException e)
        {
            result = null;
        }

        assertNotNull(result);

    }
    @Test
    public void loginBadData() throws Exception
    {
        db.getUserDao().writeUser(fakeUser);

        LoginResult result = null;
        LoginRequest request = new LoginRequest(fakeUser);
        request.setPassword("won't work");
        try
        {
            result = new LoginService(db).login(request);
        }
        catch(UnauthorizedException e)
        {
            result = null;
        }

        catch(DatabaseException e)
        {
            result = null;
        }

        assertNull(result);
    }

}