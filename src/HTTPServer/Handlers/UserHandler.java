package HTTPServer.Handlers;
import Database.DatabaseInitializer;
import GameClasses.Gameworld;
import GameClasses.User;
import HTTPServer.Utils.ExtractUsername;
import HTTPServer.Utils.UserJsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// Custom handler for the "/user" path
public class UserHandler implements HttpHandler {
    DatabaseInitializer database = new DatabaseInitializer("MonsterTradingCards", "postgres", "eeeeeeee");
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Get the input stream from the request
        InputStream requestBody = exchange.getRequestBody();

        // Read the input stream and convert it to a string
        String jsonData = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);

        // Get the User from the Substring
        String path = exchange.getRequestURI().getPath();
        String subUsername = path.substring(path.lastIndexOf('/') + 1);

        // Get the headers from the request
        Map<String, List<String>> headers = exchange.getRequestHeaders();

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

        boolean isToken = false;

        //Iterate through the Headers and get the token
        String token = "";
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            //System.out.println(entry.getKey() + ": " + entry.getValue().toString());
            if(entry.getKey().equals("Authorization")) {
                token = entry.toString();
                isToken = true;
            }
        }

        //Get the username form the token
        String tokenUsername = ExtractUsername.extract(token);

        if ("POST".equals(exchange.getRequestMethod())) {
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
            //connection and insertion into dataBase
            try {
                if(database.userExists(username)) {
                    response.append("User already exists!");
                }
                else {
                    String insertUserQuery = "INSERT INTO users (username, passwordd) VALUES "
                            + "('" + username + "', '" + password + "')";
                    String insertScoreboardQuery = "INSERT INTO scoreboard VALUES "
                            + "('" + username + "', '" + 100 + "')";

                    // Insert the user
                    database.insert(insertUserQuery);

                    // Insert the user with 100 ELO points to the scoreboard
                    database.insert(insertScoreboardQuery);
                    response.append("\n").append("Register successful!");
                }
            } catch (Exception ex) {
                response.append("\n").append("Register unsuccessful!");
                System.out.println("Database Error: " + ex);
            }
        }
        // Here the data of the user is printed
        else if("GET".equals(exchange.getRequestMethod())) {
            if(isToken && tokenUsername.equals(subUsername)) {
                response.append(subUsername + " data:");
                List<String> userData = database.getDataFromUserTable(subUsername);
                if(userData.get(1) == null) {
                    response.append("\nNo data for " + subUsername + " yet!");
                }
                else {
                    response.append("\nName: " + userData.get(0) + " | Bio: " + userData.get(1) + " | Image: " + userData.get(2));
                }
            }
            else {
                if(!isToken)
                    response.append("Invalid! No token!");
                else if(!tokenUsername.equals(subUsername))
                    response.append("Invalid! Token and Username don't match!");
                else
                    response.append("Error!");
            }
        }
        // Here the user's data is changed
        else if("PUT".equals(exchange.getRequestMethod())) {
            if(isToken && tokenUsername.equals(subUsername)) {
                List<Map.Entry<String, String>> data = new ArrayList<>();
                // The json data is converted into a key-value pair and the values assigned to the variables
                data = getKeyValuePairsFromJson(jsonData);
                String name = "";
                String bio = "";
                String image = "";

                for (Map.Entry<String, String> pair : data) {
                    if(pair.getKey().equals("Name")) {
                        name = pair.getValue();
                    }
                    else if(pair.getKey().equals("Bio")) {
                        bio = pair.getValue();
                    }
                    else {
                        image = pair.getValue();
                    }
                }

                // The data is inserted into the entry of the given user
                try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MonsterTradingCards", "postgres", "eeeeeeee")) {
                    String sql = "UPDATE users SET name = ?, bio = ?, image = ? WHERE username = ?";
                    try (PreparedStatement statement = connection.prepareStatement(sql)) {
                        statement.setString(1, name);
                        statement.setString(2, bio);
                        statement.setString(3, image);
                        statement.setString(4, subUsername);

                        statement.executeUpdate();
                        response.append("Data changed for " + subUsername);
                    }
                } catch (SQLException e) {
                    e.printStackTrace(); // Handle the exception appropriately in a real-world application
                }
            }
            else {
                if(!isToken)
                    response.append("Invalid! No token!");
                else if(!tokenUsername.equals(subUsername))
                    response.append("Invalid! Token and Username don't match!");
                else
                    response.append("Error!");
            }
        }
        os.write(response.toString().getBytes());
        // Close the output stream and the exchange
        os.close();
        exchange.close();
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

    // Helper Method to parse the json data into a key pair list
    public static List<Map.Entry<String, String>> getKeyValuePairsFromJson(String jsonStr) {
        List<Map.Entry<String, String>> keyValuePairs = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"([^\"]+)\":\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(jsonStr);

        while (matcher.find()) {
            keyValuePairs.add(new HashMap.SimpleEntry<>(matcher.group(1), matcher.group(2)));
        }

        return keyValuePairs;
    }

}
