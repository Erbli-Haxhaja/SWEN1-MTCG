package Database;
import java.sql.*;

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

    public void insert(String insertUserQuery) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(insertUserQuery);

            System.out.println("Sample user inserted successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
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
}