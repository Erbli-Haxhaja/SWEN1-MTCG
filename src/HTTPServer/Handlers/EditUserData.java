package HTTPServer.Handlers;

import Database.DatabaseInitializer;
import GameClasses.Card;
import GameClasses.Gameworld;
import HTTPServer.Utils.ExtractUsername;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


// Custom handler for the "/transactions/packages" path
public class EditUserData implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Edit user data");
        DatabaseInitializer database = new DatabaseInitializer("MonsterTradingCards", "postgres", "eeeeeeee");
        //declare the client response
        StringBuilder response = new StringBuilder("");
        // Get the headers from the request
        Map<String, List<String>> headers = exchange.getRequestHeaders();

        // Get the output stream to write the response
        OutputStream os = exchange.getResponseBody();
        // Get the input stream from the request
        InputStream requestBody = exchange.getRequestBody();

        String path = exchange.getRequestURI().getPath();
        String u = path.substring(path.lastIndexOf('/') + 1);

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
        String username = ExtractUsername.extract(token);

        if ("GET".equals(exchange.getRequestMethod())) {

            if(isToken) {
                response.append(u + " data:");

            }
            else {
                response.append("Invalid! No token!");
            }

        }
        // Deck configuration
        else if("PUT".equals(exchange.getRequestMethod())) {
            String jsonData = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
            ArrayList<String> cardIds = jsonToArrayList(jsonData);

            response.append("Data changed for " + u);

        }

        // Send the response back to the client
        exchange.sendResponseHeaders(200, response.length());
        os.write(response.toString().getBytes());
    }

    public ArrayList<String> jsonToArrayList(String jsonData) {
      // Remove the enclosing square brackets and split the string
      String[] elements = jsonData.substring(1, jsonData.length() - 1).split(", ");

      // Create an ArrayList and add the elements
      ArrayList<String> arrayList = new ArrayList<>();
      for (String element : elements) {
          String cleanedElement = element.replaceAll("^\"|\"$", "");
          arrayList.add(cleanedElement);
      }
      return arrayList;
  }

}
