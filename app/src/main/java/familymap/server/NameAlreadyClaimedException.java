package familymap.server;

/**
 * Created by Garret R Gang on 11/18/19.
 */

public class NameAlreadyClaimedException extends Exception
{
    public NameAlreadyClaimedException(String name)
    {
        super("ERROR: " + name + " is already used ");
    }
}
