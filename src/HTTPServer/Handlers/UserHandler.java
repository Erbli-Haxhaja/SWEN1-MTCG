package HTTPServer.Handlers;
import Database.DatabaseInitializer;
import HTTPServer.Utils.UserJsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;


// Custom handler for the "/user" path
public class UserHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("POST".equals(exchange.getRequestMethod()) || "GET".equals(exchange.getRequestMethod())) {
            // Get the input stream from the request
            InputStream requestBody = exchange.getRequestBody();

            // Read the input stream and convert it to a string
            String jsonData = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);

            // Print the JSON data
            System.out.println("Received JSON data:");
            System.out.println(jsonData);

            // Get the query parameters from the json string
            Map<String, String> queryParams = splitQuery(jsonData);
            // Get the output stream to write the response
            OutputStream os = exchange.getResponseBody();

            // Set the response headers
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, 0);

            // Write the response body and include the get parameters in the response
            StringBuilder response = new StringBuilder("");
            String username = "", password = "";
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key.equals("Password")) {
                    password = value;
                } else if (key.equals("Username")) {
                    username = value;
                } else {
                    break;
                }
            }
            System.out.println("\nUsername: " + username);
            System.out.println("\nPassword: " + password);
            //connection and insertion into dataBase
            try {
                DatabaseInitializer database = new DatabaseInitializer("MonsterTradingCard", "postgres", "erblienes2A");
                String insertUserQuery = "INSERT INTO users (username, passwordd) VALUES "
                        + "('" + username + "', '" + password + "')";
                database.insert(insertUserQuery);
                response.append("\n").append("Register successful!");
            } catch (Exception ex) {
                response.append("\n").append("Register unsuccessful!");
                System.out.println("Database Error: " + ex);
            }

            os.write(response.toString().getBytes());

            // Close the output stream and the exchange
            os.close();
            exchange.close();
        }
    }


    // Helper method to split query parameters
    private Map<String, String> splitQuery(String query) {
        try {
            return UserJsonUtil.jsonToMap(query);
        } catch(Exception e) {
            Map<String, String> m = null;
            return m;
        }
    }
}
