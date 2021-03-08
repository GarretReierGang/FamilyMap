package server.service.load;

import org.junit.Test;

import server.ServiceTestSuite;

import static org.junit.Assert.assertEquals;

/**
 * Created by Garret R Gang on 10/23/19.
 */
public class LoadServiceTest extends ServiceTestSuite
{
    @Test
    public void load() throws Exception
    {
        LoadRequest lr = new LoadRequest(generated_data.people, fakeUser, generated_data.events);
        LoadResult result = new LoadService(db).load(lr);

        db.open();
        assertEquals(String.format("Successfully added %d users, %d persons, and %d events to the database", 1, generated_data.people.length, generated_data.events.length),
                result.getMessage());
    }
}