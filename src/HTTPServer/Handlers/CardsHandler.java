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
public class CardsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        DatabaseInitializer database = new DatabaseInitializer("MonsterTradingCards", "postgres", "eeeeeeee");

        if ("POST".equals(exchange.getRequestMethod()) || "GET".equals(exchange.getRequestMethod())) {
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
                //Print the token
                System.out.println("Token: " + token);

                //Get the username form the token
                String username = ExtractUsername.extract(token);
                System.out.println("Username: " + username);

                response.append(username + " cards:");

                //add the packages to the user from then token
                //iterate through all the logged in users
                for (int i = 0; i < Gameworld.users.size(); i++) {
                    //find the correct user (the user from the authorization token)
                    if(Gameworld.users.get(i).getUsername().equals(username)) {
                        ArrayList<Card> stack = new ArrayList<>();
                        //take the user's stack
                        stack = Gameworld.users.get(i).getStack();
                        //append all the cards to the response
                        for (Card card : stack) {
                            response.append("\nName: " + card.getName() + " | Damage: " + card.getDamage() + " | Card Type: " + card.getCardType() + " | Element Type: " + card.getElementType());
                        }
                    }
                }
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
                os.close();
                exchange.close();
            }
        }
    }
}
