package server.service.fill;
/**
 *Contains the results of the fill result
 *<pre>
 *<b>Domain:</b>
 * message	: Success/Failure message.
 *</pre>
 *
 */
public class FillResult
{
    String message;
    /**
     *Fill request constructor
     *<pre>
     * if either parameters are negative, this will contain an error report
     *</pre>
     *@param peopleAdded number of persons added to the database
     *@param eventsAdded number of events added to the database
     */
    public FillResult(int peopleAdded, int eventsAdded)
    {
        message = String.format("Successfully added %d persons and %d events to the database", peopleAdded, eventsAdded );
    }

    public FillResult(String error_msg)
    {
        System.out.println("Fill Error!");
        message = "error "+error_msg;
    }
    /**
     *displays information about the success of a fill request
     *
     */
    public String getMessage()
    {
        return message;
    }
}