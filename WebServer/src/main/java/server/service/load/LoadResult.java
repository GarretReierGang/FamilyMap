package server.service.load;

public class LoadResult
{
    String message;
    /**
     *Constructer for error messages
     *
     * @param msg the message to display
     */
    public LoadResult(String msg)
    {
        message = msg;
    }
    /**
     * constructor for successes
     *
     *@param users -number of users added
     *@param persons -number of persons created
     *@param events - number of events created
     */
    public LoadResult(int users, int persons, int events)
    {
        message = String.format("Successfully added %d users, %d persons, and %d events to the database",users, persons, events);
    }


    /**
     *message getter
     *
     */
    public String getMessage()
    {
        return message;
    }

}