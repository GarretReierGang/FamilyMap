package server.dao;

public class DatabaseException extends Exception
{
    String error;
    /**
     *@param error a description of the problem encountered.
     */
    public DatabaseException(String error)
    {
            this.error= error;
    }

    /**
     * @return the error encountered
     */
    public String getError()
    {
        return this.error;
    }
}