package Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseInitializer {

    private String URL = "jdbc:postgresql://localhost:5432/";
    private String USER;
    private String PASSWORD;
    public DatabaseInitializer(String databaseName, String username, String password) {
        this.URL+=databaseName;
        this.USER = username;
        this.PASSWORD = password;
    }

    public void insert(String query) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //returns packages with a certain id as a list of objects
    public List<List<Object>> getPackage(int id) {
        List<List<Object>> resultList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT id, name, damage, cardtype, elementtype FROM cards where packageid = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                ResultSet resultSet = statement.executeQuery();

                if(resultSet.isBeforeFirst()) {
                    while(resultSet.next()) {
                        String idd = resultSet.getString("id");
                        String name = resultSet.getString("name");
                        int damage = resultSet.getInt("damage");
                        String cardType = resultSet.getString("cardtype");
                        String elementType = resultSet.getString("elementtype");

                        // Create an object with the extracted values
                        // and add it to the resultList
                        List<Object> entry = new ArrayList<>();
                        entry.add(idd);
                        entry.add(name);
                        entry.add(damage);
                        entry.add(cardType);
                        entry.add(elementType);
                        resultList.add(entry);
                    }
                }
                else {
                    resultList = null;
                }

            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately in a real-world application
        }

        return resultList;
    }

    // Returns a List of the data from the given user
    public List<String> getDataFromUserTable(String username) {
        List<String> resultList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT name, bio, image FROM users where username = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                ResultSet resultSet = statement.executeQuery();
                while(resultSet.next()) {
                    // Extract all the columns
                    String name = resultSet.getString("name");
                    String bio = resultSet.getString("bio");
                    String image = resultSet.getString("image");

                    // Add the columns to the List
                    resultList.add(name);
                    resultList.add(bio);
                    resultList.add(image);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately in a real-world application
        }

        return resultList;
    }

    // Returns a List of the Scoreboard
    public List<HashMap<String, String>> getDataFromScoreboard() {
        List<HashMap<String, String>> resultList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT * FROM scoreboard ORDER BY elo DESC";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();
                while(resultSet.next()) {
                    // Extract all the columns
                    String name = resultSet.getString("username");
                    int elo = resultSet.getInt("elo");

                    // Add the columns to the List
                    HashMap<String, String> map = new HashMap<>();
                    map.put(name, Integer.toString(elo));
                    resultList.add(map);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately in a real-world application
        }

        return resultList;
    }

    // Returns a List of the Stats for the
    public List<String> getDataFromStats(String username) {
        List<String> resultList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT * FROM stats where username = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                ResultSet resultSet = statement.executeQuery();
                while(resultSet.next()) {
                    // Extract all the columns
                    String name = resultSet.getString("username");
                    int elo = resultSet.getInt("elo");
                    int wins = resultSet.getInt("wins");
                    int draws = resultSet.getInt("draws");
                    int losses = resultSet.getInt("losses");

                    // Add the columns to the List
                    resultList.add(name);
                    resultList.add(Integer.toString(elo));
                    resultList.add(Integer.toString(wins));
                    resultList.add(Integer.toString(draws));
                    resultList.add(Integer.toString(losses));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately in a real-world application
        }

        return resultList;
    }

    //deletes from table
    public void deleteFromTable(String tableName) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "DELETE FROM " + tableName;
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately in a real-world application
        }
    }

    //checks if there is an entry in the database with the given username and password
    public boolean authenticateUser(String username, String password) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT * FROM users WHERE username = ? and passwordd = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, password);

                try (ResultSet resultSet = statement.executeQuery()) {
                    // Iterate through all entries to check for matches
                    while (resultSet.next()) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately in a real-world application
        }

        return false; // Default to false in case of any errors
    }

    public boolean userExists(String username) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);

                try (ResultSet resultSet = statement.executeQuery()) {
                    // Check if there is a match
                    if(resultSet.next()) {
                        //if there is one return true
                        return true;
                    }
                    else {
                        //if not false
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately in a real-world application
        }

        return false; // Default to false in case of any errors
    }
}