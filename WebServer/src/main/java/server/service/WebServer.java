package server.service;

/**
 * Created by Garret R Gang on 10/22/19.
 */

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import server.service.clear.ClearHandler;
import server.service.event.EventHandler;
import server.service.fill.FillHandler;
import server.service.load.LoadHandler;
import server.service.login.LoginHandler;
import server.service.person.PersonHandler;
import server.service.register.RegisterHandler;

public class WebServer
{
    //the maximum number of connections legal to queue.
    private static final int MAX_WAITING_CONNECTIONS = 12;


    private HttpServer server;

    private void run (String port_number)
    {
        try {
            // Create a new HttpServer object.
            // Rather than calling "new" directly, we instead create
            // the object by calling the HttpServer.create static factory method.
            // Just like "new", this method returns a reference to the new object.
            server = HttpServer.create(
                    new InetSocketAddress(Integer.parseInt(port_number)),
                    MAX_WAITING_CONNECTIONS);
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Indicate that we are using the default "executor".
        // This line is necessary, but its function is unimportant for our purposes.
        server.setExecutor(null);

        // Log message indicating that the server is creating and installing
        // its HTTP handlers.
        // The HttpServer class listens for incoming HTTP requests.  When one
        // is received, it looks at the URL path inside the HTTP request, and
        // forwards the request to the handler for that URL path.
        System.out.println("Creating contexts");



        server.createContext("/event", new EventHandler());
        server.createContext("/person", new PersonHandler());
        server.createContext("/user/register", new RegisterHandler());
        server.createContext("/user/login", new LoginHandler());
        server.createContext("/load", new LoadHandler());
        server.createContext("/fill", new FillHandler());
        server.createContext("/clear", new ClearHandler());
        // Log message indicating that the HttpServer is about the start accepting
        // incoming client connections.
        System.out.println("Starting server");


        server.createContext("/", new FMSBaseHandler());
        // Tells the HttpServer to start accepting incoming client connections.
        // This method call will return immediately, and the "main" method
        // for the program will also complete.
        // Even though the "main" method has completed, the program will continue
        // running because the HttpServer object we created is still running
        // in the background.
        server.start();

        // Log message indicating that the server has successfully started.
        System.out.println("Server started");
    }
    public static void main(String[] args) {

        String portNumber = "9090";
        if(args.length >0)portNumber= args[0];
        System.out.println(portNumber);
        //String portNumber = "9090";
        new WebServer().run(portNumber);
    }
}
