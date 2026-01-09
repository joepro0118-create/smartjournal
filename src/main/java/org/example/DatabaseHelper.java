package org.example;

import java.sql.*;

public class DatabaseHelper {
    // For SQLite (creates a file named smartjournal.db in your project folder)
    private static final String URL = "jdbc:sqlite:smartjournal.db";

    // IF USING MYSQL, USE THIS INSTEAD:
    // private static final String URL = "jdbc:mysql://localhost:3306/your_db_name";
    // private static final String USER = "root";
    // private static final String PASS = "password";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void createTable() {
        // SQL to create a users table if it doesn't exist
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                + "email TEXT PRIMARY KEY,"
                + "display_name TEXT NOT NULL,"
                + "password TEXT NOT NULL" // This will store the hashed password
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Database: Connected and table checked.");
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    public static void insertUser(User user) {
        String sql = "INSERT OR IGNORE INTO users(email, display_name, password) VALUES(?,?,?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getDisplayName());
            pstmt.setString(3, user.getPassword());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Insert Error: " + e.getMessage());
        }
    }

    public static User getUser(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("email"),
                        rs.getString("display_name"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.out.println("Query Error: " + e.getMessage());
        }
        return null;
    }
    public static void removeDeletedUsers(java.util.List<String> activeEmails) {
        String selectSql = "SELECT email FROM users";
        String deleteSql = "DELETE FROM users WHERE email = ?";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSql)) {

            while (rs.next()) {
                String dbEmail = rs.getString("email");

                // If the email in the DB is NOT in the text file list, delete it
                if (!activeEmails.contains(dbEmail)) {
                    System.out.println("Syncing: Removing deleted user -> " + dbEmail);
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                        deleteStmt.setString(1, dbEmail);
                        deleteStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Sync Error: " + e.getMessage());
        }
    }
}