package server.service.login;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;

import server.dao.DatabaseException;
import server.dao.UnauthorizedException;
import server.service.ErrorMsgJson;
import server.service.FMSBaseHandler;

/**
 * Created by Garret Gang on 10/10/19.
 */

public class LoginHandler extends FMSBaseHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
        String message;
        System.out.println("Running /Login/");
        try
        {
            Reader reader = new InputStreamReader( exchange.getRequestBody() );
            LoginRequest lr = gson.fromJson(reader, LoginRequest.class);


            try {
                LoginResult result = new LoginService().login(lr);
                message = gson.toJson(result);
            }
            catch(UnauthorizedException|DatabaseException error)
            {
                message = gson.toJson(new ErrorMsgJson("error bad password or username"));
            }

        }
        catch(JsonSyntaxException error)
        {
            message = gson.toJson(new ErrorMsgJson("error bad password or username"));
        }

        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        OutputStream respBody = exchange.getResponseBody();
        writeString( message, respBody);
        respBody.close();

    }
}
