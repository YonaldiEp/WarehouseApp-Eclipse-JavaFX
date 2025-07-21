package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Kelas utilitas untuk koneksi database.
public class DatabaseConnection {
    // Konfigurasi koneksi.
    private static final String URL = "jdbc:mysql://localhost:3306/warehouse";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Method untuk mendapatkan koneksi.
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}