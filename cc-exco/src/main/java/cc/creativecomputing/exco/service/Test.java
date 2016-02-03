package cc.creativecomputing.exco.service;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Test {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("10.1.8.217",4598), 0);
        server.createContext("/test", new HttpHandler() {
            @Override
            public void handle(HttpExchange t) throws IOException {
                String response = "This is the response";
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });
        server.setExecutor(null); // creates a default executor
        server.start();
    }

  

}