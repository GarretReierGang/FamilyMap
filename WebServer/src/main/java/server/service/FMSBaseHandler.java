package server.service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by Garret R Gang on 10/22/19.
 */

public class FMSBaseHandler implements HttpHandler
{
    protected Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
        //this whole section is not the best way of doing this, the web id file getting method used here is very hackable.
        String fileId = exchange.getRequestURI().getPath();
        System.out.println(fileId);
        if(fileId.equals("/") )
        {
            fileId = "index.html";
        }
        File file =new  File("web/"+fileId);
        if (!file.canRead()) {
            String response = "Error 404 File not found.";
            exchange.sendResponseHeaders(404, 0);
            OutputStream output = exchange.getResponseBody();
            output.write(response.getBytes());
            output.flush();
            output.close();
        } else {
            exchange.sendResponseHeaders(200, 0);
            OutputStream output = exchange.getResponseBody();
            FileInputStream fs = new FileInputStream(file);
            final byte[] buffer = new byte[0x10000];
            int count = 0;
            while ((count = fs.read(buffer)) >= 0) {
                output.write(buffer, 0, count);
            }
            output.flush();
            output.close();
            fs.close();
        }

    }
    /*
        The writeString method shows how to write a String to an OutputStream.
    */
    protected void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }


    public String getAuthToken(HttpExchange exchange)
    {
        return exchange.getRequestHeaders().getFirst("Authorization");
    }

}
