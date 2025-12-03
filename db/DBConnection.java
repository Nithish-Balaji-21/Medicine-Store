package db;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection getConnection() {
        try {
            // Load credentials from environment variables or use defaults for demo
            String url = System.getenv("DB_URL");
            String user = System.getenv("DB_USER");
            String password = System.getenv("DB_PASSWORD");
            
            // Demo defaults (for development/testing only)
            if (url == null) url = "jdbc:mysql://localhost/medicalstore";
            if (user == null) user = "root";
            if (password == null) password = "root";
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            System.err.println("Database connection failed. Running in demo mode.");
            e.printStackTrace();
            return null;
        }
    }
}
