package server.service.login;

import server.model.User;

/**
 *A construct for holding request information
 *<pre>
 *<b>Domain</b>
 *  userName:	String
 *  password:	String
 */
public class LoginRequest
{
    String userName;
    String password;
    /**
     *Only Constructer for LoginRequest
     *
     *
     */
    LoginRequest()
    {
    }

    public LoginRequest(String userName, String password)
    {
        this.userName = userName;
        this.password = password;
    }

    LoginRequest(User u)
    {
        userName = u.getUserName();
        password = u.getPassword();
    }
    /**
     *userName getter
     */
    String getUserName()
    {
        return userName;
    }
    /**
     *password Getter
     */
    String getPassword()
    {
        return password;
    }

    void setPassword(String password)
    {
        this.password = password;
    }
}