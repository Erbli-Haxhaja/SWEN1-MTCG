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
public class DeckHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        DatabaseInitializer database = new DatabaseInitializer("MonsterTradingCards", "postgres", "eeeeeeee");
        //declare the client response
        StringBuilder response = new StringBuilder("");
        // Get the headers from the request
        Map<String, List<String>> headers = exchange.getRequestHeaders();

        // Get the output stream to write the response
        OutputStream os = exchange.getResponseBody();
        // Get the input stream from the request
        InputStream requestBody = exchange.getRequestBody();
        // Set the response headers

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

        //Get the username form the token
        String username = ExtractUsername.extract(token);

        // Show unconfigured Deck (5 first cards of the stack are added in the deck)
        if ("GET".equals(exchange.getRequestMethod())) {

            if(isToken) {
                response.append(username + " deck:");

                //add the packages to the user from then token
                //iterate through all the logged in users
                for (int i = 0; i < Gameworld.users.size(); i++) {
                    //find the correct user (the user from the authorization token)
                    if(Gameworld.users.get(i).getUsername().equals(username)) {
                        if(!Gameworld.users.get(i).isConfigrured()) {
                            Gameworld.users.get(i).unconfiguredDeck();
                        }
                        ArrayList<Card> deck = new ArrayList<>();
                        //take the user's stack
                        deck = Gameworld.users.get(i).getDeck();
                        //append all the cards to the response
                        for (Card card : deck) {
                            response.append("\nName: " + card.getName() + " | Damage: " + card.getDamage() + " | Card Type: " + card.getCardType() + " | Element Type: " + card.getElementType());
                        }
                        Gameworld.users.get(i).setConfigrured(true);
                    }
                }
            }
            else {
                response.append("Invalid! No token!");
            }

        }
        // Deck configuration
        else if("PUT".equals(exchange.getRequestMethod())) {
            String jsonData = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
            ArrayList<String> cardIds = jsonToArrayList(jsonData);

            //iterate through all the logged in users
            for (int i = 0; i < Gameworld.users.size(); i++) {
                //find the correct user (the user from the authorization token)
                if(Gameworld.users.get(i).getUsername().equals(username)) {
                    System.out.println("\nAdding cards for " + username);
                    if(cardIds.size() == 4) {
                        System.out.println("There are 4 cards to be added!");
                        ArrayList<Card> deck = new ArrayList<>();
                        for (String id : cardIds) {
                            for(Card card : Gameworld.users.get(i).getStack()) {
                                //System.out.println("Comparing: " + card.getId() + " => " + id);
                                if(id.equals(card.getId())) {
                                    System.out.println(username + " added card");
                                    deck.add(card);
                                    database.insert("INSERT INTO userdeck VALUES ('" + username + "'," + "'" + id + "')");
                                }
                            }
                        }
                        if(deck.size() == 4) {
                            Gameworld.users.get(i).getDeck().clear();
                            Gameworld.users.get(i).setDeck(deck);
                            response.append("\nCards added to deck");
                        }
                        else {
                            response.append("Only " + deck.size() + " cards matched!");
                            response.append("\nCards not added to deck");
                        }

                    }
                    else {
                        response.append("You have to put 4 cards to configure!");
                    }

                }
            }

        }

        // Send the response back to the client
        exchange.sendResponseHeaders(200, response.length());
        os.write(response.toString().getBytes());
        os.close();
        exchange.close();
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
