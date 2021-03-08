package server.service.person;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

import server.dao.Database;
import server.dao.DatabaseException;
import server.dao.UnauthorizedException;
import server.model.Person;
import server.service.ErrorMsgJson;
import server.service.FMSBaseHandler;

/**
 * Created by Garret R Gang on 10/22/19.
 */

public class PersonHandler extends FMSBaseHandler
{
    final int PREFIX_LENGTH = "/person/".length();
    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
        String message;
        //check to see if the method is the correct kind for this http
        if(exchange.getRequestMethod().toLowerCase().equals("get"))
        {

            //figure out what kind of event request it is.
            URI url = exchange.getRequestURI();
            String tail = url.getPath();
            //get the authtoken from the headers in the http request.
            String auth_token = getAuthToken(exchange);
            System.out.println(auth_token);
            Headers h = exchange.getRequestHeaders();


            try {
                System.out.println(tail);
                //two possible event requests, branch for one or the other
                if (tail.length() <= PREFIX_LENGTH) {
                    System.out.println("Running /person/");
                    PeopleRequest request = new PeopleRequest(auth_token);
                    Person result[] = (new PersonService(new Database(Database.DEFAULT_DATABASE)) ).getPeople(request);
                    if(result.length > 0 )
                    {
                        //Emulating a struct that has Person[] data as one of it's storage types
                        message = "{ \"data\":" + gson.toJson(result) + "}";
                    }
                    else
                    {
                        message = "{ \"message\":\"error No Persons found\" }" ;
                    }
                } else {
                    //request is /person/[person_id]

                    System.out.println("Running /person/{ID}");

                    //grab the event_id from the uri
                    String p_id = tail.substring(PREFIX_LENGTH, tail.length());

                    //generate a request object
                    PersonRequest request = new PersonRequest(auth_token, p_id);

                    //ask the service to resolve the request
                    Person result = (new PersonService(new Database(Database.DEFAULT_DATABASE)) ).getPerson(request);

                    //save the response.
                    if(result == null)
                    {
                        message = "{ \"message\":\" error Person not found\" }";
                    }
                    else {
                        message = gson.toJson(result);
                    }
                }
            }

            catch (UnauthorizedException e)
            {
                ErrorMsgJson result = new ErrorMsgJson("error Unauthorized access");
                message = gson.toJson(result);
            }
            catch (DatabaseException e)
            {
                ErrorMsgJson result = new ErrorMsgJson("error Database Exception");
                message = gson.toJson(result);
            }
        }
        else
        {
            //there was an error with their http.
            message = "{ " +
                    "\"message\": \"error: You done messed up and posted when you should have gotten\" " +
                    "}";

            //let the server know it should send this to the client.
        }

        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        OutputStream respBody = exchange.getResponseBody();
        writeString(message, respBody);
        respBody.close();

    }
}
