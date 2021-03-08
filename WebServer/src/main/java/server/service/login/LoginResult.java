package server.service.login;

import server.service.register.RegisterResult;

/**
 *Class containg success/failure results of login
 *
 */
public class LoginResult extends RegisterResult
{
    public LoginResult(String a_token, String user_name, String person_ID)
    {
        super(a_token,person_ID, user_name);
     }

}