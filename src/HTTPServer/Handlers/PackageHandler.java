package HTTPServer.Handlers;

import Database.DatabaseInitializer;
import HTTPServer.Utils.PackageJsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


// Custom handler for the "/packages" path
public class PackageHandler implements HttpHandler {
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
            List<Triple<String, String, Double>> queryParams = splitQuery(jsonData);
            // Get the output stream to write the response
            OutputStream os = exchange.getResponseBody();

            // Set the response headers
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, 0);

            // Write the response body and include the get parameters in the response
            StringBuilder response = new StringBuilder("");
            response.append(jsonData);

            for (Triple<String, String, Double> triplet : queryParams) {
                // Access triplet components
                String id = triplet.getLeft();
                String name = triplet.getMiddle();
                Double damage = triplet.getRight();

                //connection and insertion into dataBase
                try {
                    DatabaseInitializer database = new DatabaseInitializer("MonsterTradingCard", "postgres", "erblienes2A");
                    String insertPackageQuery = "INSERT INTO packages (id, name, damage) VALUES "
                            + "('" + id + "', '" + name + "', " + damage + ")";
                    System.out.println("Insert Query: " + insertPackageQuery);
                    System.out.println("--------");
                    database.insert(insertPackageQuery);
                    response.append("\n").append("Package Insertion successful!");
                } catch (Exception ex) {
                    response.append("\n").append("Package Insertion unsuccessful!");
                    System.out.println("Database Error: " + ex);
                }

            }

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
