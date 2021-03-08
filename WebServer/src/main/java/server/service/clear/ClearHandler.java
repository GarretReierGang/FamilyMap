package server.service.clear;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import server.dao.Database;
import server.dao.DatabaseException;
import server.service.FMSBaseHandler;


/**
 * Created by Garret R Gang on 10/22/19.
 */

public class ClearHandler extends FMSBaseHandler
{
    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
        String message;
        System.out.println("Running /clear/");
        if( exchange.getRequestMethod().toLowerCase().equals("post") )
        {
            Gson gson = new Gson();
            ClearResult r;
            try {
                r = new ClearService(new Database(Database.DEFAULT_DATABASE)).clear();
            }
            catch(DatabaseException e)
            {
                r = new ClearResult("error: could not connect to database.");
            }
            message = gson.toJson(r);
        }
        else
        {
            message = "{ \"message\":\"error can only clear on a post\" }";
        }

        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        OutputStream respBody = exchange.getResponseBody();
        writeString( message, respBody);
        respBody.close();
    }
}
