package server.service.fill;

/**
 *a class that contains all of the relevant data for a fill request
 *
 */
public class FillRequest
{
    String userName;
    int generations;

    /**
     *Fill Requests only constructor
     *
     *<pre>
     *<b>Constraints</b>
     *  userName must be in the database, generations must be a non negative number.
     *</pre>
     */
    public FillRequest(String user_name, int generations)
    {
        this.userName = user_name;
        this.generations = generations;
    }
    /**
     *userName getter
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     *generations getter
     */
    public int getGenerations()
    {
        return generations;
    }
}