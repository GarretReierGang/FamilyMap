package server.service.event;

/**
 *A class containing all the information needed to load a person from the database
 *
 */
public class EventsRequest
{
    String authority;
    /**
     *Creates a request to get a particular person
     */
    public EventsRequest(String authority)
    {
        this.authority = authority;
    }
    /**
     *Gets the authority this uses to operate.
     */
    public String getAuthority()
    {
        return authority;
    }

}