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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


// Custom handler for the "/transactions/packages" path
public class ScoreboardHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        DatabaseInitializer database = new DatabaseInitializer("MonsterTradingCards", "postgres", "eeeeeeee");

        if ("GET".equals(exchange.getRequestMethod())) {
            //declare the client response
            StringBuilder response = new StringBuilder("");
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
                //Get the username form the token
                String username = ExtractUsername.extract(token);
                System.out.println("Username: " + username);


                response.append(username + " scoreboard:");
                List<String> data = database.getDataFromScoreboard(username);
                response.append("\nELO Value: " + data.get(1));

            }
            else {
                response.append("Invalid! No token!");
            }


            // Get the input stream from the request
            InputStream requestBody = exchange.getRequestBody();

            // Send the response back to the client
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.toString().getBytes());
            }
        }
    }
}
