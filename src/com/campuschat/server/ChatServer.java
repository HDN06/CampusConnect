
package com.campuschat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    public static final int PORT = 5000;

    private final Map<String, String> registeredUsers =
            new ConcurrentHashMap<>();

    private final Map<String, ClientHandler> connectedClients =
            new ConcurrentHashMap<>();

    private volatile boolean running = true;

    public ChatServer() {
        // Demo accounts
        registeredUsers.put("user1", "pass123");
        registeredUsers.put("user2", "pass123");
    }

    public void start() {
        System.out.println("CampusChat server starting on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (running) {
                Socket socket = serverSocket.accept();

                ClientHandler handler = new ClientHandler(this, socket);
                Thread thread = new Thread(handler, "ClientHandler");
                thread.start();
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Server error: " + e.getMessage());
            }
        }
    }

    public boolean registerUser(String username, String password) {
        if (username == null || password == null
                || username.isBlank() || password.isBlank()
                || username.contains("|") || password.contains("|")) {
            return false;
        }

        return registeredUsers.putIfAbsent(username, password) == null;
    }

    public boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        String storedPassword = registeredUsers.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }

    public boolean addConnectedClient(
            String username, ClientHandler handler) {
        return connectedClients.putIfAbsent(username, handler) == null;
    }

    public void removeConnectedClient(String username) {
        if (username != null) {
            connectedClients.remove(username);

            broadcastSystemMessage(username + " disconnected.");
            broadcastUserList();

            System.out.println(username + " disconnected.");
        }
    }

    public ClientHandler getClient(String username) {
        return connectedClients.get(username);
    }

    public void broadcastMessage(String sender, String message) {
        try {
            DatabaseManager.saveMessage(sender, null, message);
        } catch (Exception e) {
            System.err.println(
                    "Could not save public message: " + e.getMessage());
        }

        String encodedMessage = encode(message);
        String formattedMessage =
                "MESSAGE|" + sender + "|" + encodedMessage;

        for (ClientHandler client : connectedClients.values()) {
            client.send(formattedMessage);
        }

        System.out.println(sender + ": " + message);
    }

    public void sendPrivateMessage(
            String sender, String recipient, String message) {

        ClientHandler recipientHandler = connectedClients.get(recipient);

        if (recipientHandler == null) {
            ClientHandler senderHandler = connectedClients.get(sender);

            if (senderHandler != null) {
                senderHandler.send(
                        "ERROR|User '" + recipient + "' is not online.");
            }
            return;
        }

        try {
            DatabaseManager.saveMessage(sender, recipient, message);
        } catch (Exception e) {
            System.err.println(
                    "Could not save private message: " + e.getMessage());
        }

        String encodedMessage = encode(message);
        String formattedMessage =
                "PRIVATE|" + sender + "|" + recipient + "|"
                        + encodedMessage;

        recipientHandler.send(formattedMessage);

        ClientHandler senderHandler = connectedClients.get(sender);
        if (senderHandler != null && !sender.equals(recipient)) {
            senderHandler.send(formattedMessage);
        }

        System.out.println(
                "[Private] " + sender + " -> " + recipient + ": " + message);
    }

    public List<String[]> getChatHistory(String username) {
        return DatabaseManager.getHistoryForUser(username);
    }

    public void sendChatHistory(String username, ClientHandler client) {
        try {
            List<String[]> history = getChatHistory(username);

            for (String[] item : history) {
                String sender = item[0];
                String receiver = item[1] == null ? "" : item[1];
                String message = item[2];
                String timestamp = item[3];

                client.send(
                        "HISTORY|" + sender + "|" + receiver + "|"
                                + timestamp + "|" + encode(message));
            }
        } catch (Exception e) {
            System.err.println(
                    "Could not load chat history for " + username
                            + ": " + e.getMessage());

            client.send("ERROR|Could not load chat history.");
        }
    }

    private String encode(String message) {
        return java.util.Base64.getEncoder().encodeToString(
                message.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    public void broadcastSystemMessage(String message) {
        String formattedMessage = "SYSTEM|" + message;

        for (ClientHandler client : connectedClients.values()) {
            client.send(formattedMessage);
        }
    }

    public void broadcastUserList() {
        String users = String.join(",", connectedClients.keySet());
        String formattedMessage = "USERS|" + users;

        for (ClientHandler client : connectedClients.values()) {
            client.send(formattedMessage);
        }
    }

    public static void main(String[] args) {
        new ChatServer().start();
    }
}
