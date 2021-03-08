package server.service.event;

/**
 *A class containing all the information needed to load a person from the database
 *
 */
public class EventRequest
{
    String authority;
    String eventId;
    /**
     *Creates a request to get a particular person
     * @param authority authtoken that is being used
     * @param id event id being requested
     */
    public EventRequest(String authority, String id)
    {
        this.authority = authority;
        this.eventId = id;
    }
    /**
     *Gets the authority this uses to operate.
     */
    public String getAuthority()
    {
        return authority;
    }

    /**
     *Gets the id of the event this is requesting.
     */
    public String getEventId()
    {
        return eventId;
    }
}