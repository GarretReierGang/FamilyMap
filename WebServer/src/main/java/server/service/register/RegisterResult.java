package server.service.register;

/**
 *Contains the results of a register request
 *<pre>
 *<b>Domain:</b>
 * message	: Success/Failure message.
 *</pre>
 *
 */
public class RegisterResult
{
    String authToken;
    String userName;
    String personID;
    /**
     *Constructer
     *
     */
    public RegisterResult(String authority, String person_id, String user_name)
    {
        this.authToken = authority;
        this.personID = person_id;
        this.userName = user_name;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUserName() {
        return userName;
    }

    public String getPersonID() {
        return personID;
    }
}