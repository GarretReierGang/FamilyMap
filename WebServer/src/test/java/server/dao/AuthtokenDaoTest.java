package server.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Garret R Gang on 10/26/19.
 */
public class AuthtokenDaoTest
{
    Database sess;

    @Before
    public void setUp() throws Exception
    {
        sess = new Database("fms_database.sqlite3");
        sess.destroyTables();
        sess.createTables();
    }

    @After
    public void tearDown() throws Exception
    {
        sess.close(false);
    }

    @Test
    public void login() throws Exception
    {

    }

}