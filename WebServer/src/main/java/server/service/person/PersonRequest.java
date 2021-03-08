package server.service.person;

/**
 *A class containing all the information needed to load a person from the database
 *
 */
public class PersonRequest
{
    String authToken;
    String personId;
    /**
     *Creates a request to get a particular person
     */
    public PersonRequest(String authority, String id)
    {
        authToken = authority;
        personId = id;
    }

     /**
     *Gets the authority this uses to operate.
     */
    public String getAuthority()
    {
        return authToken;
    }

    /**
     *Gets the id of the person this is requesting.
     */
    public String getId()
    {
        return personId;
    }
}