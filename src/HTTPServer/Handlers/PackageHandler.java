package HTTPServer.Handlers;

import Database.DatabaseInitializer;
import GameClasses.Gameworld;
import HTTPServer.Utils.ExtractUsername;
import HTTPServer.Utils.PackageJsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// Custom handler for the "/packages" path
public class PackageHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("POST".equals(exchange.getRequestMethod()) || "GET".equals(exchange.getRequestMethod())) {
            // Get the headers from the request
            Map<String, List<String>> headers = exchange.getRequestHeaders();

            //Iterate through the Headers and get the token
            String token = "";
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                //System.out.println(entry.getKey() + ": " + entry.getValue().toString());
                if(entry.getKey().equals("Authorization")) {
                    token = entry.getValue().toString();
                }
            }

            StringBuilder response = new StringBuilder("");
            // Get the output stream to write the response
            OutputStream os = exchange.getResponseBody();
            // Get the input stream from the request
            InputStream requestBody = exchange.getRequestBody();
            // Set the response headers
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, 0);

            String username = ExtractUsername.extract(token);
            System.out.println("Username: " + username);
            boolean insertionStatus = false;
            if(username.equals("admin")) {
                // Read the input stream and convert it to a string
                String jsonData = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);

                // Print the JSON data
                System.out.println("Received JSON data:");
                System.out.println(jsonData);

                // Get the query parameters from the json string
                List<Triple<String, String, Double>> queryParams = splitQuery(jsonData);

                //response.append(jsonData);
                DatabaseInitializer database = new DatabaseInitializer("MonsterTradingCards", "postgres", "eeeeeeee");
                int lastid = Gameworld.addPackageLastId;
                Gameworld.addPackageLastId++;
                for (Triple<String, String, Double> triplet : queryParams) {
                    // Access triplet components
                    String id = triplet.getLeft();
                    String name = triplet.getMiddle();
                    Double damage = triplet.getRight();
                    String cardType = "";
                    String elementType = "";
                    if(name.endsWith("Spell")) {
                        cardType = "spell";
                    }
                    else if(name.endsWith("monster")){
                        cardType = "monster";
                    }
                    else {
                        cardType = "monster";
                    }

                    if(name.startsWith("Water")) {
                        elementType = "water";
                    } else if (name.startsWith("Fire")) {
                        elementType = "fire";
                    }
                    else {
                        elementType = "normal";
                    }

                    //connection and insertion into dataBase
                    try {
                        String insertPackageQuery = "INSERT INTO cards (packageid, id, name, damage, elementtype, cardtype) VALUES "
                                + "('" + lastid + "', '" + id + "', '" + name + "', " + damage + ", '" + elementType + "', '" + cardType + "')";
                        System.out.println("Insert Query: " + insertPackageQuery);
                        System.out.println("--------");
                        database.insert(insertPackageQuery);
                        insertionStatus = true;
                    } catch (Exception ex) {
                        insertionStatus = false;
                        System.out.println("Database Error: " + ex);
                    }
                }
            }
            else {
                response.append("Only admin can add packages!");
            }

            if(insertionStatus)
                response.append("\n").append("Package Insertion successful!");
            else
                response.append("\n").append("Package Insertion unsuccessful!");

            os.write(response.toString().getBytes());

            // Close the output stream and the exchange
            os.close();
            exchange.close();
        }
    }


    // Helper method to split query parameters
    private List<Triple<String, String, Double>> splitQuery(String query) {
        try {
            return PackageJsonUtil.convertJsonToTriplets(query);
        } catch(Exception e) {
            List<Triple<String, String, Double>> m = null;
            return m;
        }
    }
}
