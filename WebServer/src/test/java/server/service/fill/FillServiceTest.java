package server.service.fill;

import org.junit.Test;

import java.io.Console;

import server.ServiceTestSuite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by Garret R Gang on 10/24/19.
 */
public class FillServiceTest extends ServiceTestSuite
{
    @Test
    public void fillWithGoodName() throws Exception
    {
        int generations = 10;
        writeData();
        FillRequest request = new FillRequest(fakeUser.getUserName(), 10);
        FillResult result = new FillService(db).fill(request);
        assertEquals(result.getMessage().indexOf("error"), -1);//no error occured
    }


    @Test
    public void fillWithBadName() throws Exception
    {
        int generations = 10;
        writeData();
        FillRequest request = new FillRequest("the_name_doesn't_exist_in_the_db", 10);
        FillResult result = new FillService(db).fill(request);
        assertNotEquals(result.getMessage().indexOf("error"), -1);
    }

}