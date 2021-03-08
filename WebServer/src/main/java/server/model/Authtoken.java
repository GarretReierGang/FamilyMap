package server.model;

public class Authtoken
{
    String code;
    User owner;

    /**
     * Authoken Constructer
     *<pre>
     *<b>Constraints on the Input</b>
     *  owner and code are not null.
     *</pre>
     * @param owner User that the code belongs
     * @param code The string code.
     */
    public Authtoken(User owner, String code)
    {
        this.code = code;
        this.owner = owner;
    }

    /**
     * Gives the user that this authtoken is for.
     */
    public User getUser()
    {
        return this.owner;
    }
    public String getUserName()
    {
        return owner.getUserName();
    }

    /**
     * Shows the string authcode.
     */
    public String getCode()
    {
        return code;
    }

}