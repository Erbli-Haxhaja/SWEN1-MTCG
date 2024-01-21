package Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UnitTests {
    @Test
    @Order(1)
    void testUserRegistration() throws IOException {
        // JSON data
        String json = "{\"Username\":\"erbli\", \"Password\":\"erbli1234\"}";

        // Send a POST request to the server to register a new user
        String response = test(json, "POST", "users", "");

        // Check the response
        assertEquals("Register successful!", response);
    }

    @Test
    @Order(2)
    void testExistingUserRegistration() throws IOException {
        // JSON data
        String json = "{\"Username\":\"erbli\", \"Password\":\"1111\"}";

        // Send a POST request to the server to register a existing user
        // Should fail
        String response = test(json, "POST", "users", "");

        // Check the response
        assertEquals("User already exists!", response);
    }

    @Test
    @Order(3)
    void testNoTokenCheck() throws IOException {
        // JSON data
        String json = "";

        // Send a POST request to the server to test the token security of user registration (no token)
        String response = test(json, "GET", "users/erbli", "");

        // Check the response
        assertEquals("Invalid! No token!", response);
    }

    @Test
    @Order(4)
    void testWrongToken() throws IOException {
        // JSON data
        String json = "";

        // Send a POST request to the server to test the token security of user registration (wrong token)
        String response = test(json, "GET", "users/erbli", "Bearer aaa-mtcgToken");

        // Check the response
        assertEquals("Invalid! Token and Username don't match!", response);
    }

    @Test
    @Order(5)
    void testGetUserInfoWithoutAddingFirst() throws IOException {
        // JSON data
        String json = "";

        // Send a POST request to the server to get the user info without adding such info first
        String response = test(json, "GET", "users/erbli", "Bearer erbli-mtcgToken");

        // Check the response
        assertEquals("No data for erbli yet!", response);
    }

    @Test
    @Order(6)
    void testEditUserData() throws IOException {
        // JSON data
        String json = "{\"Name\": \"Erbli Haxhaja\",  \"Bio\": \"Coder\", \"Image\": \":-)\"}";

        // Send a POST request to the server to edit the user data
        String response = test(json, "PUT", "users/erbli", "Bearer erbli-mtcgToken");

        // Check the response
        assertEquals("Data changed for erbli", response);
    }

    @Test
    @Order(7)
    void testSuccessfulLogin() throws IOException {
        // JSON data
        String json = "{\"Username\":\"erbli\", \"Password\":\"erbli1234\"}";

        // Send a POST request to the server to login a user
        String response = test(json, "POST", "sessions", "");

        // Check the response
        assertEquals("Login successful!", response);
    }

    @Test
    @Order(8)
    void testUnSuccessfulLogin() throws IOException {
        // JSON data
        String json = "{\"Username\":\"erbli\", \"Password\":\"wrongpass\"}";

        // Send a POST request to the server to login a user with wrong credentials
        String response = test(json, "POST", "sessions", "");

        // Check the response
        assertEquals("Login unsuccessful!", response);
    }

    @Test
    @Order(9)
    void testSuccessfulPackageCreation() throws IOException {
        // JSON data
        String json = "[{\"Id\":\"845f0dc7-37d0-426e-994e-43fc3ac83c08\", \"Name\":\"WaterGoblin\", \"Damage\": 10.0}, {\"Id\":\"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\", \"Name\":\"Dragon\", \"Damage\": 50.0}, {\"Id\":\"e85e3976-7c86-4d06-9a80-641c2019a79f\", \"Name\":\"WaterSpell\", \"Damage\": 20.0}, {\"Id\":\"1cb6ab86-bdb2-47e5-b6e4-68c5ab389334\", \"Name\":\"Ork\", \"Damage\": 45.0}, {\"Id\":\"dfdd758f-649c-40f9-ba3a-8657f4b3439f\", \"Name\":\"FireSpell\",    \"Damage\": 25.0}]";

        // Send a POST request to the server to create a new package successfuly
        String response = test(json, "POST", "packages", "Bearer admin-mtcgToken");

        // Check the response
        assertEquals("Package Insertion successful!", response);
    }


    // Wrong JSON Data inserted
    @Test
    @Order(10)
    void testUnsuccessfulPackageCreation() throws IOException {
        // JSON data => wrong JSON Data (no ID in the beginning)
        String json = "\"[{\"Id\":, \"Name\":\"WaterGoblin\", \"Damage\": 10.0}, {\"Id\":\"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\", \"Name\":\"Dragon\", \"Damage\": 50.0}, {\"Id\":\"e85e3976-7c86-4d06-9a80-641c2019a79f\", \"Name\":\"WaterSpell\", \"Damage\": 20.0}, {\"Id\":\"1cb6ab86-bdb2-47e5-b6e4-68c5ab389334\", \"Name\":\"Ork\", \"Damage\": 45.0}, {\"Id\":\"dfdd758f-649c-40f9-ba3a-8657f4b3439f\", \"Name\":\"FireSpell\",    \"Damage\": 25.0}]\"";

        // Send a POST request to the server to create a new package unsuccessfuly
        String response = test(json, "POST", "packages", "Bearer admin-mtcgToken");

        // Check the response
        assertEquals("Package Insertion unsuccessful!", response);
    }

    @Test
    @Order(11)
    void testNoAdminPackageCreation() throws IOException {
        // JSON data
        String json = "\"[{\"Id\":\"845f0dc7-37d0-426e-994e-43fc3ac83c08\", \"Name\":\"WaterGoblin\", \"Damage\": 10.0}, {\"Id\":\"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\", \"Name\":\"Dragon\", \"Damage\": 50.0}, {\"Id\":\"e85e3976-7c86-4d06-9a80-641c2019a79f\", \"Name\":\"WaterSpell\", \"Damage\": 20.0}, {\"Id\":\"1cb6ab86-bdb2-47e5-b6e4-68c5ab389334\", \"Name\":\"Ork\", \"Damage\": 45.0}, {\"Id\":\"dfdd758f-649c-40f9-ba3a-8657f4b3439f\", \"Name\":\"FireSpell\",    \"Damage\": 25.0}]\"";

        // Send a POST request to the server to create a new package unsuccessfuly (not admin user)
        String response = test(json, "POST", "packages", "Bearer erbli-mtcgToken");

        // Check the response
        assertEquals("Only admin can add packages!", response);
    }

    @Test
    @Order(12)
    void testUnSuccessfulPackageAcquireNoToken() throws IOException {
        // JSON data
        String json = "";

        // Send a POST request to the server to acquire a package unsuccessfuly (no token)
        String response = test(json, "POST", "transactions/packages", "");

        // Check the response
        assertEquals("Invalid! No token!", response);
    }

    @Test
    @Order(13)
    void testSuccessfulPackageAcquire() throws IOException {
        // JSON data
        String json = "";

        // Send a POST request to the server to acquire a package successfuly
        String response = test(json, "POST", "transactions/packages", "Bearer erbli-mtcgToken");

        // Check the response
        assertEquals("Package1 acquired for erbli!", response);
    }

    @Test
    @Order(14)
    void testUnSuccessfulPackageAcquireNoMorePackages() throws IOException {
        // JSON data
        String json = "";

        // Send a POST request to the server to acquire a package unsuccessfuly (no packages left)
        String response = test(json, "POST", "transactions/packages", "Bearer erbli-mtcgToken");

        // Check the response
        assertEquals("No more packages to acquire!", response);
    }

    @Test
    @Order(15)
    void testCardsShowNoToken() throws IOException {
        // JSON data
        String json = "";

        // Send a POST request to the server to show the acquired cards unsuccessfuly (no token)
        String response = test(json, "POST", "card", "");

        // Check the response
        assertEquals("Invalid! No token!", response);
    }


    @Test
    @Order(16)
    void testDeckShowNoToken() throws IOException {
        // JSON data
        String json = "";

        // Send a POST request to the server to show deck unsuccessfuly (no token)
        String response = test(json, "GET", "deck", "");

        // Check the response
        assertEquals("Invalid! No token!", response);
    }

    @Test
    @Order(17)
    void testDeckConfigNotEnoughMatchingCards() throws IOException {
        // JSON data
        String json = "[\\\"dfdd758f-649c-40f9-ba3a-8657f4b3439f\\\", \\\"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\\\", \\\"e85e3976-7c86-4d06-9a80-641c2019a79f\\\", \\\"171f6076-4eb5-4a7d-b3f2-2d650cc3d237\\\"]";

        // Send a POST request to the server to configure the deck unsuccessfuly (not enough matching cards from the stack)
        String response = test(json, "PUT", "deck", "Bearer erbli-mtcgToken");

        // Check the response
        assertEquals("Only 0 cards matched!Cards not added to deck", response);
    }

    @Test
    @Order(18)
    void testDeckConfigNotEnoughCards() throws IOException {
        // JSON data
        String json = "[\"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\", \"e85e3976-7c86-4d06-9a80-641c2019a79f\", \"171f6076-4eb5-4a7d-b3f2-2d650cc3d237\"]";

        // Send a POST request to the server to configure the deck unsuccessfuly (not enough cards in the config json data, should be 4)
        String response = test(json, "PUT", "deck", "Bearer erbli-mtcgToken");

        // Check the response
        assertEquals("You have to put 4 cards to configure!", response);
    }

    @Test
    @Order(19)
    void testSuccessfulDeckConfig() throws IOException {
        // JSON data
        String json = "[\"845f0dc7-37d0-426e-994e-43fc3ac83c08\", \"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\", \"e85e3976-7c86-4d06-9a80-641c2019a79f\", \"dfdd758f-649c-40f9-ba3a-8657f4b3439f\"]";

        // Send a POST request to the server to configure the deck successfuly
        String response = test(json, "PUT", "deck", "Bearer erbli-mtcgToken");

        // Check the response
        assertEquals("Cards added to deck", response);
    }

    @Test
    @Order(20)
    void testUnsuccessfulBattleStartNoToken() throws IOException {
        // JSON data
        String json = "";

        // Send a POST request to the server to start a battle unsuccessfuly (no token)
        String response = test(json, "POST", "battles", "");

        // Check the response
        assertEquals("Invalid! No token!", response);
    }

    @Test
    @Order(21)
    void testBattleWaitingForSecondUser() throws IOException {
        // JSON data
        String json = "";

        // Send a POST request to the server to start a battle successfuly only for 1 user and wait for the second
        String response = test(json, "POST", "battles", "Bearer erbli-mtcgToken");

        // Check the response
        assertEquals("erbli joined the battle!Waiting for the 2nd player!", response);
    }

    public String test(String jsonInputString, String requestMethod, String path, String token) throws IOException {
        // URL of the server
        URL url = new URL("http://localhost:10001/" + path);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        // Set up the request properties
        con.setRequestMethod(requestMethod);
        con.setRequestProperty("Content-Type", "application/json");
        if(!token.equals(""))
            con.setRequestProperty("Authorization", token);
        con.setDoOutput(true);


        if(!jsonInputString.equals("")) {
            // Send the request
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }

        // Read the response
        StringBuilder response = new StringBuilder();
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        con.disconnect();

        return response.toString();
    }
}