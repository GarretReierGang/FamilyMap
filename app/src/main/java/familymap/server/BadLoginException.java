package familymap.server;

/**
 * Created by Garret R Gang on 11/18/19.
 */

public class BadLoginException extends Exception
{
    public BadLoginException()
    {
        super("Invalid Password or username");
    }
}
