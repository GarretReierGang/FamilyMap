package familymap.server;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import server.model.Event;
import server.model.Person;
import server.service.login.LoginRequest;
import server.service.register.RegisterRequest;
import server.service.register.RegisterResult;

/**
 * Created by Garret R Gang on 11/18/19.
 */

public class ServerProxy
{
    Model data;
    String sessionToken;
    String userId;
    String userPersonId;
    Gson gson;


    public ServerProxy(Model data)
    {
        gson = new Gson();
        this.data = data;
        userPersonId = data.getUserId();
        //if(userPersonId!= null) Log.d("server Proxy", data.getUserId());
        sessionToken = data.getSessionToken();
    }

    public Model login(LoginRequest lr) throws BadLoginException, ServerError
    {
        //exchange.getRequestHeaders().getFirst("Authorization");
        try
        {

            URL url = new URL("http://" + getServerHost() + ":" + getServerPort() + "/user/login");

            // Start constructing our HTTP request
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            // Specify that we are sending an HTTP GET request
            http.setRequestMethod("GET");
            // Indicate that this request will not contain an HTTP request body
            http.setDoOutput(true);

            // Add an auth token to the request in the HTTP "Authorization" header
            // Specify that we would like to receive the server's response in JSON
            // format by putting an HTTP "Accept" header on the request (this is not
            // necessary because our server only returns JSON responses, but it
            // provides one more example of how to add a header to an HTTP request).
            // Connect to the server and send the HTTP request
            http.connect();
            OutputStream reqBody = http.getOutputStream();
            // Write the JSON data to the request body
            writeString( gson.toJson(lr), reqBody);            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            reqBody.close();
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                    //at this point both login and register are the same.
                setAuthtoken(http);
                try
                {
                    refresh();
                }
                catch(BadSessionId e)
                {
                    throw new ServerError("Server shutdown while trying to load events and people");
                }
            }
            else {
                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                System.out.println("ERROR: " + http.getResponseMessage());
                throw new BadLoginException();
            }
        }
        catch(IOException e)
        {
            System.out.println("ERROR: could not connect to server");
            //analyze actual problem later... it could be gson or server based. For now assume server, because if there is a json problem its the servers fault.
            throw new ServerError("Could not connect to server");
        }
        return data;
    }

    public Model register(RegisterRequest rr) throws ServerError, NameAlreadyClaimedException
    {
        //exchange.getRequestHeaders().getFirst("Authorization");
        try
        {
            URL url = new URL("http://" + getServerHost() + ":" + getServerPort() + "/user/register");
            System.out.println(url.getHost() + ":" + url.getPort());
            // Start constructing our HTTP request
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            // Specify that we are sending an HTTP POST request
            http.setRequestMethod("POST");
            // Indicate that this request will contain an HTTP request body
            http.setDoOutput(true);


            // Connect to the server and send the HTTP request
            http.connect();
            OutputStream reqBody = http.getOutputStream();
            // Write the JSON data to the request body
            writeString( gson.toJson(rr), reqBody);            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            reqBody.close();
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                //at this point both login and register are the same.
                setAuthtoken(http);
                try
                {
                    refresh();
                }
                catch(BadSessionId e)
                {
                    throw new ServerError("Server shutdown while trying to load events and people");
                } catch (BadLoginException e) {
                    throw new NameAlreadyClaimedException("Name was claimed");
                }
            }
            else {
                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                System.out.println("ERROR: " + http.getResponseMessage());
                throw new NameAlreadyClaimedException("Name was claimed");
            }
        }
        catch(IOException e)
        {
            //do something about this later
            System.out.println("ERROR: could not connect to server");
            //Log.d("register",e.getMessage() );
            //analyze actual problem later... it could be gson or server based. For now assume server, because if there is a json problem its the servers fault.
            throw new ServerError("Could not connect to server");
        }
        return data;
    }

    private void setAuthtoken(HttpURLConnection http)
    {
        try
        {
            Reader reader = new InputStreamReader( http.getInputStream() );
            RegisterResult rr = gson.fromJson( reader, RegisterResult.class);

            this.sessionToken = rr.getAuthToken();
            this.data.setSessionToken( sessionToken );
            this.userPersonId = rr.getPersonID();
            this.userId = rr.getUserName();
            //data.setUserId(userId);
        }
        catch(IOException e)
        {
            //do something about this later
            System.out.println("ERROR: could not connect to server");
        }
    }

    public Model refresh() throws BadSessionId, BadLoginException
    {
        data.setEvents(getEvents() );
        data.setPeople( getPeople() );
        data.setUserPerson(userPersonId);
        return data;
    }

    private Event[] getEvents() throws BadSessionId, BadLoginException
    {
        try
        {
            URL url = new URL("http://" + getServerHost() + ":" + getServerPort() + "/event/");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.addRequestProperty("Authorization", sessionToken);
            http.setDoOutput(false);
            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                Reader reader = new InputStreamReader(http.getInputStream());
                EventStorageClass temp = gson.fromJson(reader, EventStorageClass.class);
                Event[] events = temp.data;
                if (events == null)
                {
                    throw new BadLoginException();
                }
                return events;
            }
            else {
                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                System.out.println("ERROR: " + http.getResponseMessage());
                throw new BadSessionId();
            }
        }
        catch(IOException error)
        {
            //do something later
            System.out.println("ERROR: could not connect to server");
        }
        return null;
    }

    private Person[] getPeople() throws BadSessionId
    {
        try
        {
            URL url = new URL("http://" + getServerHost() + ":" + getServerPort() + "/person/");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.addRequestProperty("Authorization", sessionToken);
            http.setDoOutput(false);
            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                Reader reader = new InputStreamReader(http.getInputStream());
                PersonTransferclass temp = gson.fromJson(reader, PersonTransferclass.class );
                return temp.data;
            }
            else {
                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                System.out.println("ERROR: " + http.getResponseMessage());
                throw new BadSessionId();
            }
        }
        catch(IOException error)
        {
            //do something later
            System.out.println("ERROR: could not connect to server");
        }
        return null;
    }


    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }


    public String getServerHost()
    {
        return data.getServerHost();
    }

    public void setServerHost(String serverHost)
    {
        this.data.setServerHost(serverHost);
    }

    public String getServerPort()
    {
        return this.data.getServerPort();
    }

    public void setServerPort(String serverPort)
    {
        this.data.setServerHost(serverPort);
    }
}
