
package com.campuschat.server;

public class CRUDTest {

    public static void main(String[] args) {

        // CREATE
        boolean saved = DatabaseManager.saveMessage(
                "user1", "user2", "Hello from CampusConnect!");

        System.out.println("CREATE: " + saved);

        // READ
        System.out.println("\nMessages before update:");
        DatabaseManager.getMessages();

        // UPDATE message ID 1
        boolean updated = DatabaseManager.updateMessage(
                1, "This message was updated!");

        System.out.println("\nUPDATE message 1: " + updated);

        // READ again
        System.out.println("\nMessages after update:");
        DatabaseManager.getMessages();

        // DELETE message ID 1
        boolean deleted = DatabaseManager.deleteMessage(1);

        System.out.println("\nDELETE message 1: " + deleted);

        // READ again
        System.out.println("\nMessages after delete:");
        DatabaseManager.getMessages();
    }
}
