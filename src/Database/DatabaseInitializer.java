package Database;
import org.apache.commons.lang3.tuple.Triple;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
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

    public void createTable(String tableQuery) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

             statement.executeUpdate(tableQuery);

             System.out.println("Table created successfully.");

        } catch (SQLException e) {
            System.out.println("Connection refused: " + e);
        }
    }

    public void insert(String query) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(query);

            System.out.println("Insertion successfull.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //Get the id of the last added package
    public int getLastId() {
        int id = 0;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT packageid FROM packages ORDER BY packageid DESC LIMIT 1";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    id = resultSet.getInt("packageid");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately in a real-world application
        }

        return id;
    }
    public int getFirstId() {
        int id = 0;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT packageid FROM packages ORDER BY packageid ASC LIMIT 1";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    id = resultSet.getInt("packageid");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately in a real-world application
        }

        return id;
    }

    //returns packages with a certain id as a list of objects
    public List<List<Object>> getPackage(int id) {
        List<List<Object>> resultList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT id, name, damage, elementtype FROM packages where packageid = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                ResultSet resultSet = statement.executeQuery();

                //check if maybe there are no packages left to add
                if(!resultSet.next()) {
                    resultList=null;
                }
                else {
                    while(resultSet.next()) {
                        String idd = resultSet.getString("id");
                        String name = resultSet.getString("name");
                        int damage = resultSet.getInt("damage");
                        String elementtype = resultSet.getString("elementtype");

                        // Create an object with the extracted values
                        // and add it to the resultList
                        List<Object> entry = new ArrayList<>();
                        entry.add(idd);
                        entry.add(name);
                        entry.add(damage);
                        entry.add(elementtype);
                        resultList.add(entry);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately in a real-world application
        }

        return resultList;
    }

    //deletes packages
    public void deletePackage(int id) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "DELETE FROM packages where packageid = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
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