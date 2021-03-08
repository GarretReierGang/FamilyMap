package server.service.clear;

/**
 *A class used to pass out success/failure from using the clear functionality.
 *
 */
public class ClearResult
{
    String message;
    /**
     *only Constructor for the class
     *@param message contains a success or error mesage;
     */
    public ClearResult(String message)
    {
        this.message = message;
    }

    /**
     *Message Getter
     */
    public String getMessage()
    {
        return message;
    }
}