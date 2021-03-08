package server.service.person;

/**
 *A class containing all the information needed to load a person from the database
 *
 */
public class PeopleRequest
{
    String authority;
    /**
     *Creates a request to get all the people belonging to a user
     */
    public PeopleRequest(String authority)
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