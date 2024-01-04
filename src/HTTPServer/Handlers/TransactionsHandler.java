package HTTPServer.Handlers;

import Database.DatabaseInitializer;
import GameClasses.Card;
import GameClasses.Gameworld;
import HTTPServer.Utils.ExtractUsername;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


// Custom handler for the "/transactions/packages" path
public class TransactionsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        DatabaseInitializer database = new DatabaseInitializer("MonsterTradingCards", "postgres", "eeeeeeee");

        if ("POST".equals(exchange.getRequestMethod()) || "GET".equals(exchange.getRequestMethod())) {
            //declare the client response
            String response = "";
            // Get the headers from the request
            Map<String, List<String>> headers = exchange.getRequestHeaders();

            boolean isToken = false;
            //Iterate through the Headers and get the token
            String token = "";
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                //System.out.println(entry.getKey() + ": " + entry.getValue().toString());
                if(entry.getKey().equals("Authorization")) {
                    token = entry.getValue().toString();
                    isToken = true;
                }
            }

            if(isToken) {
                //Print the token
                System.out.println("Token: " + token);

                //Get the username form the token
                String username = ExtractUsername.extract(token);
                System.out.println("Username: " + username);

                //add the packages to the user from then token
                //iterate through all the logged in users
                for (int i = 0; i < Gameworld.users.size(); i++) {
                    //find the correct one (the user from the authorization token)
                    if(Gameworld.users.get(i).getUsername().equals(username)) {
                        // First check if the user has enough money to buy the package
                        if(Gameworld.users.get(i).getCoins() >= 5) {
                            List<List<Object>> packageData = new ArrayList<>();
                            packageData = database.getPackage(Gameworld.packageFirstId);
                            if(packageData != null) {
                                // add the packages
                                for (List<Object> entry : packageData) {
                                    System.out.println("Card:");
                                    System.out.println("ID: " + entry.get(0));
                                    System.out.println("Name: " + entry.get(1));
                                    System.out.println("Damage: " + entry.get(2));
                                    System.out.println("ElementType: " + entry.get(3));
                                    System.out.println("");
                                    // Create cards with the data Name (elementType, damage)
                                    Card card =  new Card(entry.get(0).toString(), entry.get(1).toString(), Integer.parseInt(entry.get(2).toString()), entry.get(3).toString());
                                    // Add the created card to the user's stack
                                    Gameworld.users.get(i).addToStack(card);

                                    // Insert the card into the userstack table
                                    String query = "INSERT INTO userstack VALUES ('" + Gameworld.users.get(i).getUsername() + "', '" + entry.get(0) + "')";
                                    database.insert(query);
                                }
                                // Set the response
                                response = "Package" + Gameworld.packageFirstId + " accuired for " + username + "!";
                                Gameworld.packageFirstId++;
                                // Remove 5 coins from the user
                                Gameworld.users.get(i).setCoins(Gameworld.users.get(i).getCoins() - 5);
                                System.out.println(username + " has " + Gameworld.users.get(i).getCoins() + " coins left!");
                            }
                            else {
                                response = "No more packages to acquire!";
                            }
                        }
                        else {
                            response = "No money!";
                        }
                    }
                }
            }
            else {
                response = "Invalid! No token!";
            }
            // Get the input stream from the request
            InputStream requestBody = exchange.getRequestBody();

            // Send the response back to the client
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
