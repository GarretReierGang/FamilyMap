package server.dao;

public class UnauthorizedException extends Exception
{
    String user;
    String error;

    /**
     * @param uid User who caused the error.
     * @param error ; should be something meaningful, like Event baptism of GoodLookin
     */
    public UnauthorizedException(String uid, String error)
    {
        this.user = uid;
        this.error = error;
    }

    /**
     *A string message containing useful error information
     * @return The error.
     */
    public String toString()
    {
        return "PermissionError: " + user + " attempted to " + error ;
    }
}