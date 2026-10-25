
package com.campuschat.server;

public class CRUDTest {

    public static void main(String[] args) {

        // CREATE
        boolean saved = DatabaseManager.saveMessage(
                "user1", "user2", "Hello from CampusConnect!");

        System.out.println("CREATE: " + saved);

        // READ
        System.out.println("\nSaved messages:");
        DatabaseManager.getMessages();

        // UPDATE and DELETE will be tested using a message ID
        // after confirming that CREATE and READ work.
    }
}
