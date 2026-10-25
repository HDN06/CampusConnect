
package com.campuschat.server;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String URL =
            "jdbc:mysql://localhost:3306/campuschat_db";

    private static final String USER = "root";
    private static final String PASSWORD = "anel@123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // CREATE: Save a new message
    public static boolean saveMessage(
            String sender, String receiver, String message) {

        String sql = "INSERT INTO messages (sender, receiver, message) "
                   + "VALUES (?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sender);
            ps.setString(2, receiver);
            ps.setString(3, message);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Save message failed: " + e.getMessage());
            return false;
        }
    }

    // READ: Retrieve all saved messages
    public static void getMessages() {

        String sql = "SELECT id, sender, receiver, message, sent_at "
                   + "FROM messages ORDER BY sent_at ASC, id ASC";

        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(
                    "ID: " + rs.getInt("id")
                    + " | Sender: " + rs.getString("sender")
                    + " | Receiver: " + rs.getString("receiver")
                    + " | Message: " + rs.getString("message")
                    + " | Time: " + rs.getTimestamp("sent_at")
                );
            }

        } catch (SQLException e) {
            System.out.println("Read messages failed: " + e.getMessage());
        }
    }

    // UPDATE: Edit a message using its ID
    public static boolean updateMessage(int id, String newMessage) {

        String sql = "UPDATE messages SET message = ? WHERE id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newMessage);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Update message failed: " + e.getMessage());
            return false;
        }
    }

    // DELETE: Delete a message using its ID
    public static boolean deleteMessage(int id) {

        String sql = "DELETE FROM messages WHERE id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Delete message failed: " + e.getMessage());
            return false;
        }
    }

    public static void testConnection() {
        try (Connection con = getConnection()) {
            System.out.println("MySQL connected successfully!");
        } catch (SQLException e) {
            System.out.println("MySQL connection failed: " + e.getMessage());
        }
    }

    // READ: Get public messages and private messages involving this user
    public static java.util.List<String[]> getHistoryForUser(
            String username) {

        java.util.List<String[]> history = new java.util.ArrayList<>();

        String sql = """
                SELECT sender, receiver, message, sent_at
                FROM messages
                WHERE receiver IS NULL
                   OR sender = ?
                   OR receiver = ?
                ORDER BY sent_at ASC, id ASC
                """;

        try (Connection con = getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, username);

            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    history.add(new String[] {
                            rs.getString("sender"),
                            rs.getString("receiver"),
                            rs.getString("message"),
                            rs.getTimestamp("sent_at").toString()
                    });
                }
            }

        } catch (SQLException e) {
            System.err.println("Could not load chat history: "
                    + e.getMessage());
        }

        return history;
    }

}
