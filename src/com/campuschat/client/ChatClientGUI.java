
package com.campuschat.client;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class ChatClientGUI extends JFrame {

    private final String username;
    private final ChatClient client;

    private static final Color BG = new Color(5, 10, 7);
    private static final Color PANEL = new Color(8, 15, 10);
    private static final Color GREEN = new Color(0, 255, 100);
    private static final Color DARK_GREEN = new Color(0, 120, 45);
    private static final Color BORDER = new Color(0, 90, 35);

    private static final Font TERMINAL_FONT =
            new Font("Monospaced", Font.PLAIN, 14);

    private static final Font TERMINAL_BOLD =
            new Font("Monospaced", Font.BOLD, 14);

    private final JTextArea messageArea = new JTextArea();
    private final JTextField inputField = new JTextField();
    private final JButton sendButton = new JButton("[ SEND ]");

    private final DefaultListModel<String> usersModel =
            new DefaultListModel<>();

    private final JList<String> usersList = new JList<>(usersModel);

    private final SimpleDateFormat timeFormat =
            new SimpleDateFormat("HH:mm:ss");

    public ChatClientGUI(String username, ChatClient client) {
        this.username = username;
        this.client = client;

        setTitle("CampusChat // " + username);
        setSize(900, 600);
        setMinimumSize(new Dimension(700, 450));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        buildInterface();

        // Register the GUI listener before requesting saved history.
        client.setMessageConsumer(this::handleServerMessage);
        client.send("HISTORY_REQUEST");

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent event) {
                client.close();
            }
        });
    }

    private void buildInterface() {
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);

        configureMessageArea();
        configureUsersList();

        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        JScrollPane usersScrollPane = new JScrollPane(usersList);

        messageScrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        usersScrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        usersScrollPane.setPreferredSize(new Dimension(210, 0));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                messageScrollPane,
                usersScrollPane);

        splitPane.setDividerLocation(670);
        splitPane.setResizeWeight(1.0);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setBackground(BG);

        add(splitPane, BorderLayout.CENTER);
        add(createInputPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new GridLayout(1, 2));
        header.setBackground(BG);
        header.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, DARK_GREEN));

        JLabel titleLabel = new JLabel("  CAMPUSCHAT // TERMINAL");
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 19));
        titleLabel.setForeground(GREEN);

        JLabel userLabel = new JLabel("● ONLINE  //  " + username + "  ");
        userLabel.setFont(TERMINAL_BOLD);
        userLabel.setForeground(GREEN);

        header.add(titleLabel);
        header.add(userLabel);

        return header;
    }

    private void configureMessageArea() {
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(TERMINAL_FONT);
        messageArea.setForeground(GREEN);
        messageArea.setBackground(BG);
        messageArea.setCaretColor(GREEN);
        messageArea.setBorder(BorderFactory.createEmptyBorder(
                15, 15, 15, 15));
    }

    private void configureUsersList() {
        usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersList.setFont(TERMINAL_FONT);
        usersList.setForeground(GREEN);
        usersList.setBackground(PANEL);
        usersList.setSelectionBackground(DARK_GREEN);
        usersList.setSelectionForeground(Color.WHITE);

        usersList.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER),
                " ONLINE_USERS ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                TERMINAL_BOLD,
                GREEN));

        usersList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                String selectedUser = usersList.getSelectedValue();

                if (selectedUser != null
                        && !selectedUser.equals(username)) {
                    inputField.setToolTipText(
                            "PRIVATE MESSAGE → " + selectedUser);
                } else {
                    inputField.setToolTipText(
                            "PUBLIC MESSAGE → ALL USERS");
                }
            }
        });
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(
                10, 10, 10, 10));

        inputField.setFont(TERMINAL_FONT);
        inputField.setForeground(GREEN);
        inputField.setBackground(BG);
        inputField.setCaretColor(GREEN);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        inputField.setPreferredSize(new Dimension(0, 42));
        inputField.setToolTipText("PUBLIC MESSAGE → ALL USERS");

        sendButton.setPreferredSize(new Dimension(100, 42));
        sendButton.setFont(TERMINAL_BOLD);
        sendButton.setForeground(GREEN);
        sendButton.setBackground(BG);
        sendButton.setBorder(BorderFactory.createLineBorder(DARK_GREEN));
        sendButton.setFocusPainted(false);
        sendButton.setOpaque(true);

        sendButton.addActionListener(event -> sendMessage());
        inputField.addActionListener(event -> sendMessage());

        panel.add(inputField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        return panel;
    }

    private void sendMessage() {
        String message = inputField.getText().trim();

        if (message.isEmpty()) {
            return;
        }

        String selectedUser = usersList.getSelectedValue();

        if (selectedUser != null && !selectedUser.equals(username)) {
            client.sendPrivateMessage(selectedUser, message);
        } else {
            client.sendPublicMessage(message);
        }

        inputField.setText("");
        inputField.requestFocusInWindow();
    }

    private void handleServerMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            if (message == null || message.isEmpty()) {
                return;
            }

            String[] parts = message.split("\\|", -1);

            switch (parts[0]) {
                case "MESSAGE":
                    if (parts.length >= 3) {
                        String decoded = decode(parts[2]);
                        appendMessage("[" + currentTime() + "] "
                                + parts[1] + ": " + decoded);
                    }
                    break;

                case "PRIVATE":
                    if (parts.length >= 4) {
                        String decoded = decode(parts[3]);
                        appendMessage("[" + currentTime()
                                + "] [PRIVATE] " + parts[1]
                                + " -> " + parts[2] + ": " + decoded);
                    }
                    break;

                case "HISTORY":
                    handleHistoryMessage(parts);
                    break;

                case "SYSTEM":
                    if (parts.length >= 2) {
                        appendSystemMessage(parts[1]);
                    }
                    break;

                case "USERS":
                    if (parts.length >= 2) {
                        updateUsers(parts[1]);
                    }
                    break;

                case "ERROR":
                case "AUTH_FAIL":
                    if (parts.length >= 2) {
                        appendSystemMessage(parts[1]);
                    }
                    break;

                case "CONNECTION_CLOSED":
                    appendSystemMessage("Disconnected from server.");
                    sendButton.setEnabled(false);
                    inputField.setEnabled(false);
                    break;

                default:
                    appendSystemMessage(message);
                    break;
            }
        });
    }

    private void handleHistoryMessage(String[] parts) {
        if (parts.length < 5) {
            return;
        }

        String sender = parts[1];
        String receiver = parts[2];
        String timestamp = parts[3];
        String message = decode(parts[4]);

        if (receiver.isEmpty()) {
            appendMessage("[" + timestamp + "] " + sender + ": " + message);
        } else {
            appendMessage("[" + timestamp + "] [PRIVATE] "
                    + sender + " -> " + receiver + ": " + message);
        }
    }

    private String decode(String encoded) {
        try {
            return new String(
                    Base64.getDecoder().decode(encoded),
                    StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return "[Unable to decode message]";
        }
    }

    private void updateUsers(String commaSeparatedUsers) {
        usersModel.clear();

        if (commaSeparatedUsers == null || commaSeparatedUsers.isBlank()) {
            return;
        }

        for (String user : commaSeparatedUsers.split(",")) {
            if (!user.isBlank()) {
                usersModel.addElement(user);
            }
        }
    }

    private void appendMessage(String message) {
        messageArea.append(message + "\n");
        messageArea.setCaretPosition(
                messageArea.getDocument().getLength());
    }

    private void appendSystemMessage(String message) {
        appendMessage("[" + currentTime() + "] [SYSTEM] " + message);
    }

    private String currentTime() {
        return timeFormat.format(new Date());
    }
}
