package familymap.server;



/**
 * Created by Garret R Gang on 11/18/19.
 */

public class BadSessionId extends Exception
{
    public BadSessionId()
    {
        super("Attempted to use invalid authtoken");
    }
}
