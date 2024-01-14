import Database.DatabaseInitializer;
import GameClasses.*;
import HTTPServer.Handlers.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

public class HTTPServer {

    public static void main(String[] args) throws IOException {
        //connect to the database created in pgadmin4
        DatabaseInitializer dataBase = new DatabaseInitializer("MonsterTradingCards", "postgres", "eeeeeeee");
        // First clear the tables
        dataBase.deleteFromTable("scoreboard");
        dataBase.deleteFromTable("userdeck");
        dataBase.deleteFromTable("userstack");
        dataBase.deleteFromTable("packages");
        dataBase.deleteFromTable("users");

        // Create a server on port 10001
        HttpServer server = HttpServer.create(new InetSocketAddress(10001), 0);

        // Create a context for the "/users" path and set a handler for REGISTER
        server.createContext("/users", new UserHandler());

        // Create a context for the "/sessions" path and set a handler for LOGIN
        server.createContext("/sessions", new SessionHandler());

        // Create a context for the "/packages" path and set a handler for PACKAGE CREATION
        server.createContext("/packages", new PackageHandler());

        // Create a context for the "/transactions/packages" path and set a handler for PACKAGE ACQUIRE
        server.createContext("/transactions/packages", new TransactionsHandler());

        // Create a context for the "/card" path and set a handler for SHOW CARDS
        server.createContext("/card", new CardsHandler());

        // Create a context for the "/deck" path and set a handler for DECK SHOW and CONFIGURATION
        server.createContext("/deck", new DeckHandler());

        // Create a context for the "/scoreboard" path and set a handler for scoreboard
        server.createContext("/scoreboard", new ScoreboardHandler());

        // Set the executor to null for simplicity (default executor is used)
        server.setExecutor(null);

        // Start the server
        server.start();

        System.out.println("Server started on port 10001");
    }

}