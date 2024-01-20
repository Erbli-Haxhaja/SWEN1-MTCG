package HTTPServer.Handlers;
import Database.DatabaseInitializer;
import GameClasses.Gameworld;
import HTTPServer.Utils.ExtractUsername;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// Custom handler for the "/transactions/packages" path
public class BattleHandler implements HttpHandler {

    // The user who initiates the battle
    private String waitingUser = null;
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        DatabaseInitializer database = new DatabaseInitializer("MonsterTradingCards", "postgres", "eeeeeeee");

        if ("POST".equals(exchange.getRequestMethod())) {
            //declare the client response
            StringBuilder response = new StringBuilder("");
            // Get the headers from the request
            Map<String, List<String>> headers = exchange.getRequestHeaders();

            boolean isToken = false;

            // Iterate through the Headers and get the token
            String token = "";
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                if(entry.getKey().equals("Authorization")) {
                    token = entry.getValue().toString();
                    isToken = true;
                }
            }

            if(isToken) {
                //Get the username form the token
                String username = ExtractUsername.extract(token);
                System.out.println("Username: " + username);

                // If no battle is initialised, set the waiting user the current user initiating the battle
                if(waitingUser == null){
                    waitingUser = username;
                    response.append(username + " joined the battle!\n");
                    response.append("Waiting for the 2nd player!\n");
                }
                // If the battle has been started by 1 user, start the real battle with both users
                else {
                    // Create the Battlefield
                    Gameworld battle = new Gameworld(waitingUser, username);
                    // Start the thread for this battle
                    Thread battleThread = new Thread(battle);
                    battleThread.start();

                    try {
                        battleThread.join();
                        // Send the battle log back to the client
                        response.append(username + " joined the battle!\n");
                        response.append("The battle has been started!\n");
                        response.append("Battle Log: ");
                        response.append(battle.log);
                    } catch (InterruptedException e) {
                        response.append("Battle failed!");
                    }
                    // Reset the waiting user
                    waitingUser = null;
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
            }
        }
    }
}
