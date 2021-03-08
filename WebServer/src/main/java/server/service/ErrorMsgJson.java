package server.service;

/**
 * Created by Garret R Gang on 10/23//19.
 */

public class ErrorMsgJson {
    String message;
    public ErrorMsgJson(String message)
    {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
