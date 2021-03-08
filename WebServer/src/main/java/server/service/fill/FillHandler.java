package server.service.fill;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.StringTokenizer;

import server.dao.Database;
import server.dao.DatabaseException;
import server.service.FMSBaseHandler;

/**
 * Created by Garret R Gang on 10/22/19.
 */

public class FillHandler extends FMSBaseHandler
{
    public void handle(HttpExchange exchange) throws IOException
    {

        System.out.println("Running /Fill/");
        String path =exchange.getRequestURI().getPath();
        System.out.println(path);
        String user_name = getUserName(path);


        int generations = getGenerations(path);

        FillRequest request = new FillRequest(user_name, generations);

        FillResult result;
        try {
            result = new FillService(new Database(Database.DEFAULT_DATABASE)).fill(request);
        }
        catch(DatabaseException e)
        {
            result = new FillResult("error: Could not connect to database");
        }
        String message = gson.toJson(result);
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        //success message
        writeString(message, exchange.getResponseBody());
        exchange.getResponseBody().close();
    }


    String getUserName(String path)
    {

        StringTokenizer tokens = new StringTokenizer(path, "/");
        String temp;
        do
        {
            temp = tokens.nextToken();
        } while (!temp.equals("fill"));

        temp = tokens.nextToken();
        return temp;
    }

    int getGenerations(String path)
    {
        int output = Filler.DEFAULT_GENERATIONS;
        StringTokenizer tokens = new StringTokenizer(path, "/");
        String temp;
        do
        {
            temp = tokens.nextToken();
        } while(!temp.equals("fill"));

        tokens.nextToken();
        if(tokens.hasMoreTokens())
        {
            System.out.println("found generations");
            return Integer.parseInt(tokens.nextToken());
        }
        return output;
    }
    class InvalidUrl extends Exception
    {

    }
}
