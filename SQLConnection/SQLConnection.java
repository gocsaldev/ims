package SQLConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/zenekarleltar";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection connect() {
        Connection connection = null;

        try {
            // Establishing a connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[SQLConnection]: Connected to the database successfully!");
        } catch (SQLException e) {
            System.out.println("[SQLConnection]: Connection failed: " + e.getMessage());
        }
        return connection; // Return the connection
    }
}
