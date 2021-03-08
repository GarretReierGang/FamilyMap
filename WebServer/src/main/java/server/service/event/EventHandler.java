package server.service.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URI;
import java.text.DecimalFormat;

import server.dao.Database;
import server.dao.DatabaseException;
import server.model.Event;
import server.service.ErrorMsgJson;
import server.service.FMSBaseHandler;


/**
 * Created by Garret R Gang on 10/22/19.
 */

public class EventHandler extends FMSBaseHandler
{
    final int PREFIX_LENGTH = "/event/".length();
    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
        String message;
        /*
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Double.class, (JsonSerializer<Double>) (src, typeOfSrc, context) -> {
            DecimalFormat df = new DecimalFormat("#.####");
            df.setRoundingMode(RoundingMode.CEILING);
            return new JsonPrimitive(Double.parseDouble(df.format(src)));
        });
        Gson gson = builder.create();*/
        //check to see if the method is the correct kind for this http
        if(exchange.getRequestMethod().toLowerCase().equals("get"))
        {

            //figure out what kind of event request it is.
            URI url = exchange.getRequestURI();
            String tail = url.getPath();
            //get the authtoken from the headers in the http request.
            String auth_token = getAuthToken(exchange);

            try {

                String response;

                //two possible event requests, branch for one or the other
                if (tail.length() <= PREFIX_LENGTH) {
                    EventsRequest request = new EventsRequest(auth_token);

                    System.out.println("Running /event/");
                    Event result[] = new EventService(new Database(Database.DEFAULT_DATABASE)).getEvents(request);
                    if (result.length > 0) {
                        //Emulating a struct that has Event[] data as one of it's storage types.
                        message = "{ \"data\":" + gson.toJson(result) + "}";
                    }
                    else {
                        message = "{ \"message\": error no events exist for the given user }";
                    }
                    System.out.println("/Event/ return: " + message);
                } else {
                    //request is /event/[event_id]

                    //grab the event_id from the uri
                    String event_id = tail.substring(PREFIX_LENGTH, tail.length());
                    System.out.println("Running /event/" + event_id);

                    //generate a request object
                    EventRequest request = new EventRequest(auth_token, event_id);

                    //ask the service to resolve the request
                    Event result = new EventService(new Database(Database.DEFAULT_DATABASE)).getEvent(request);

                    //save the response.
                    message = gson.toJson(result);
                    System.out.println("Returning event " + message);
                }
            }
            catch (DatabaseException e)
            {
                ErrorMsgJson errorMsgJson = new ErrorMsgJson("error" + e.getError());
                message = gson.toJson(errorMsgJson);
            }
        }
        else
        {
            //there was an error with their http.
            message = "{ " +
                    "\"message\": \"Error: You done messed up and posted when you should have gotten\" " +
                    "}" ;
        }

        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        OutputStream respBody = exchange.getResponseBody();
        writeString( message, respBody);
        respBody.close();
    }

}
