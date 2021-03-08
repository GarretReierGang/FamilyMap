package server.service.register;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;

import server.dao.Database;
import server.dao.DatabaseException;
import server.service.ErrorMsgJson;
import server.service.FMSBaseHandler;

/**
 * Created by Garret R Gang on 10/23//19.
 */

public class RegisterHandler extends FMSBaseHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
        String message;
        System.out.println("Running /Register/");
        try
        {
            System.out.print(exchange.getRequestBody());
            Reader reader = new InputStreamReader( exchange.getRequestBody() );
            RegisterRequest register = gson.fromJson(reader, RegisterRequest.class);


            try {
                RegisterResult result = new RegisterService(new Database(Database.DEFAULT_DATABASE)).register(register);
                message = gson.toJson(result);
            }
            catch(DatabaseException error)
            {
                ErrorMsgJson errorMsgJson = new ErrorMsgJson("error " + error.getError());
                message = gson.toJson( errorMsgJson );
            }
        }
        catch(JsonSyntaxException error)
        {
            message = gson.toJson( new ErrorMsgJson("error, bad request."));
        }
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);        OutputStream respBody = exchange.getResponseBody();
        writeString( message, respBody);
        respBody.close();
    }
}
