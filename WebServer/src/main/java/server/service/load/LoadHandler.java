package server.service.load;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import server.dao.Database;
import server.dao.DatabaseException;
import server.service.FMSBaseHandler;

/**
 * Created by Garret R Gang on 10/22/19.
 */

public class LoadHandler extends FMSBaseHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException
    {

        //i really don't care what type of request it is post, get, it donna matter what they try to do
        String body = "";
        System.out.println("Running /Load/");
        LoadRequest lr = null;
        LoadResult result;
        //this might

        try
        {
            lr = gson.fromJson(new InputStreamReader( exchange.getRequestBody() ), LoadRequest.class);
            System.out.println("Load Request = " + lr.Serialize());
            try
            {
                result = new LoadService(new Database(Database.DEFAULT_DATABASE)).load(lr);


            }
            catch(DatabaseException except)
            {

                result = new LoadResult( "Unable to load " + except.getError() );
            }
        }
        catch(JsonSyntaxException e)
        {
            result = new LoadResult("error- invalid request style");
        }
        String message = gson.toJson(result);
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        OutputStream respBody = exchange.getResponseBody();
        writeString( message, respBody );
        respBody.close();
    }


}
