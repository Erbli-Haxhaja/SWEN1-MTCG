import Database.DatabaseInitializer;
import HTTPServer.Handlers.SessionHandler;
import HTTPServer.Handlers.UserHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HTTPServer {

    public static void main(String[] args) throws IOException {
        //connect to the database created in pgadmin4
        DatabaseInitializer dataBase = new DatabaseInitializer("MonsterTradingCard", "postgres", "erblienes2A");
        //create tables
        String createUserTableQuery = "CREATE TABLE users  ("
                + "username VARCHAR(255) NOT NULL PRIMARY KEY,"
                + "passwordd VARCHAR(255) NOT NULL)";
        //String createPackagesTableQuery = "";
        //dataBase.createTable(createUserTableQuery);

        // Create a server on port 10001
        HttpServer server = HttpServer.create(new InetSocketAddress(10001), 0);

        // Create a context for the "/users" path and set a handler
        server.createContext("/users", new UserHandler());

        // Create a context for the "/sessions" path and set a handler
        server.createContext("/sessions", new SessionHandler());

        // Set the executor to null for simplicity (default executor is used)
        server.setExecutor(null);

        // Start the server
        server.start();

        System.out.println("Server started on port 10001");
    }
}