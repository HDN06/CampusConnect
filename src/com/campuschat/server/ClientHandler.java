
package com.campuschat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ClientHandler implements Runnable {

    private final ChatServer server;
    private final Socket socket;

    private BufferedReader reader;
    private PrintWriter writer;

    private String username;
    private boolean authenticated = false;

    public ClientHandler(ChatServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream(), StandardCharsets.UTF_8));

            writer = new PrintWriter(
                    socket.getOutputStream(), true, StandardCharsets.UTF_8);

            String line;

            while ((line = reader.readLine()) != null) {
                if (!authenticated) {
                    if (handleAuthentication(line)) {
                        if (authenticated) {
                            continue;
                        }
                    } else {
                        break;
                    }
                } else {
                    handleCommand(line);
                }
            }
        } catch (IOException e) {
            if (username != null) {
                System.out.println(
                        "Connection ended for " + username + ".");
            }
        } finally {
            if (authenticated && username != null) {
                server.removeConnectedClient(username);
            }

            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private boolean handleAuthentication(String line) {
        String[] parts = line.split("\\|", 3);

        if (parts.length == 0) {
            send("ERROR|Invalid request.");
            return false;
        }

        if ("REGISTER".equals(parts[0])) {
            if (parts.length != 3) {
                send("REGISTER_FAIL|Invalid registration details.");
                return false;
            }

            boolean success =
                    server.registerUser(parts[1], parts[2]);

            if (success) {
                send("REGISTER_OK|Registration successful.");
            } else {
                send("REGISTER_FAIL|Username unavailable or invalid.");
            }

            // Registration uses a one-request connection.
            return false;
        }

        if (!"LOGIN".equals(parts[0]) || parts.length != 3) {
            send("AUTH_FAIL|Invalid login request.");
            return false;
        }

        String requestedUsername = parts[1];
        String password = parts[2];

        if (!server.authenticate(requestedUsername, password)) {
            send("AUTH_FAIL|Invalid username or password.");
            return false;
        }

        if (!server.addConnectedClient(requestedUsername, this)) {
            send("AUTH_FAIL|This user is already logged in.");
            return false;
        }

        username = requestedUsername;
        authenticated = true;

        send("AUTH_OK|" + username);

        server.broadcastSystemMessage(username + " connected.");
        server.broadcastUserList();

        System.out.println(username + " connected.");
        return true;
    }

    private void handleCommand(String line) {
        if (line == null || line.isEmpty()) {
            return;
        }

        if ("HISTORY_REQUEST".equals(line)) {
            server.sendChatHistory(username, this);
            return;
        }

        if (line.startsWith("MSG|")) {
            String message = line.substring(4);

            if (!message.isBlank()) {
                server.broadcastMessage(username, message);
            }
            return;
        }

        if (line.startsWith("PRIVATE|")) {
            String[] parts = line.split("\\|", 3);

            if (parts.length < 3) {
                send("ERROR|Invalid private message.");
                return;
            }

            String recipient = parts[1];
            String message = parts[2];

            if (recipient.isBlank() || message.isBlank()) {
                send("ERROR|Recipient and message are required.");
                return;
            }

            if (recipient.equals(username)) {
                send("ERROR|You cannot send a private message to yourself.");
                return;
            }

            server.sendPrivateMessage(username, recipient, message);
            return;
        }

        send("ERROR|Unknown command.");
    }

    public synchronized void send(String message) {
        if (writer != null) {
            writer.println(message);
        }
    }
}
