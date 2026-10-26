
package com.campuschat.client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class ChatClientGUI extends JFrame {

    private final String username;
    private final ChatClient client;

    // Warm cream and white color palette
    private static final Color BG = new Color(250, 248, 243);
    private static final Color WHITE = new Color(255, 255, 255);
    private static final Color SIDEBAR = new Color(244, 241, 233);
    private static final Color SENT = new Color(229, 235, 221);
    private static final Color TEXT = new Color(45, 45, 40);
    private static final Color MUTED = new Color(125, 124, 114);
    private static final Color ACCENT = new Color(103, 125, 88);
    private static final Color BORDER = new Color(229, 224, 214);

    private static final Font NORMAL =
            new Font("SansSerif", Font.PLAIN, 14);

    private static final Font MEDIUM =
            new Font("SansSerif", Font.BOLD, 14);

    private static final Font SMALL =
            new Font("SansSerif", Font.PLAIN, 12);

    private final JTextArea messageArea = new JTextArea();
    private final JTextField inputField = new JTextField();
    private final JButton sendButton = new JButton("Send  ➤");

    private final DefaultListModel<String> usersModel =
            new DefaultListModel<>();

    private final JList<String> usersList = new JList<>(usersModel);

    private final SimpleDateFormat timeFormat =
            new SimpleDateFormat("HH:mm");

    public ChatClientGUI(String username, ChatClient client) {
        this.username = username;
        this.client = client;

        setTitle("CampusConnect | " + username);
        setSize(1000, 650);
        setMinimumSize(new Dimension(750, 480));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        buildInterface();

        // Keep the existing networking and history functionality.
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
        messageScrollPane.setBorder(BorderFactory.createEmptyBorder());
        messageScrollPane.getViewport().setBackground(WHITE);
        messageScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JScrollPane usersScrollPane = new JScrollPane(usersList);
        usersScrollPane.setBorder(BorderFactory.createEmptyBorder());
        usersScrollPane.getViewport().setBackground(SIDEBAR);
        usersScrollPane.setPreferredSize(new Dimension(220, 0));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                messageScrollPane,
                usersScrollPane
        );

        splitPane.setDividerLocation(750);
        splitPane.setResizeWeight(1.0);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setBackground(BG);
        splitPane.setDividerSize(5);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(WHITE);
        centerPanel.setBorder(new EmptyBorder(12, 16, 12, 12));
        centerPanel.add(splitPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
        add(createInputPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(16, 22, 16, 22)
        ));

        JPanel branding = new JPanel();
        branding.setLayout(new BoxLayout(branding, BoxLayout.Y_AXIS));
        branding.setBackground(WHITE);

        JLabel titleLabel = new JLabel("CampusConnect");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 21));
        titleLabel.setForeground(TEXT);

        JLabel subtitleLabel = new JLabel("A simple place to connect");
        subtitleLabel.setFont(SMALL);
        subtitleLabel.setForeground(MUTED);

        branding.add(titleLabel);
        branding.add(Box.createVerticalStrut(4));
        branding.add(subtitleLabel);

        JPanel account = new JPanel();
        account.setLayout(new BoxLayout(account, BoxLayout.Y_AXIS));
        account.setBackground(WHITE);

        JLabel onlineLabel = new JLabel("●  Online");
        onlineLabel.setFont(SMALL);
        onlineLabel.setForeground(ACCENT);
        onlineLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel userLabel = new JLabel(username);
        userLabel.setFont(MEDIUM);
        userLabel.setForeground(TEXT);
        userLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        account.add(onlineLabel);
        account.add(Box.createVerticalStrut(4));
        account.add(userLabel);

        header.add(branding, BorderLayout.WEST);
        header.add(account, BorderLayout.EAST);

        return header;
    }

    private void configureMessageArea() {
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(NORMAL);
        messageArea.setForeground(TEXT);
        messageArea.setBackground(WHITE);
        messageArea.setCaretColor(TEXT);
        messageArea.setSelectionColor(SENT);
        messageArea.setSelectedTextColor(TEXT);
        messageArea.setBorder(new EmptyBorder(12, 10, 12, 10));
    }

    private void configureUsersList() {
        usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersList.setFont(NORMAL);
        usersList.setForeground(TEXT);
        usersList.setBackground(SIDEBAR);
        usersList.setSelectionBackground(SENT);
        usersList.setSelectionForeground(TEXT);
        usersList.setFixedCellHeight(36);
        usersList.setBorder(new EmptyBorder(8, 8, 8, 8));

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 0, BORDER),
                "  ONLINE MEMBERS  ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                MEDIUM,
                TEXT
        );

        usersList.setBorder(BorderFactory.createCompoundBorder(
                titledBorder,
                new EmptyBorder(8, 8, 8, 8)
        ));

        usersList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                String selectedUser = usersList.getSelectedValue();

                if (selectedUser != null
                        && !selectedUser.equals(username)) {
                    inputField.setToolTipText(
                            "Send a private message to " + selectedUser
                    );
                    inputField.setBorder(BorderFactory.createCompoundBorder(
                            new LineBorder(ACCENT, 1),
                            new EmptyBorder(10, 12, 10, 12)
                    ));
                } else {
                    inputField.setToolTipText(
                            "Send a public message to everyone"
                    );
                    inputField.setBorder(BorderFactory.createCompoundBorder(
                            new LineBorder(BORDER, 1),
                            new EmptyBorder(10, 12, 10, 12)
                    ));
                }
            }
        });
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(12, 18, 16, 18));

        inputField.setFont(NORMAL);
        inputField.setForeground(TEXT);
        inputField.setBackground(WHITE);
        inputField.setCaretColor(TEXT);
        inputField.setPreferredSize(new Dimension(0, 46));
        inputField.setToolTipText("Send a public message to everyone");
        inputField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(10, 12, 10, 12)
        ));

        sendButton.setFont(MEDIUM);
        sendButton.setForeground(WHITE);
        sendButton.setBackground(ACCENT);
        sendButton.setPreferredSize(new Dimension(115, 46));
        sendButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        sendButton.setFocusPainted(false);
        sendButton.setOpaque(true);
        sendButton.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR
        ));

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
                        appendMessage("[" + currentTime() + "]  "
                                + parts[1] + ": " + decoded);
                    }
                    break;

                case "PRIVATE":
                    if (parts.length >= 4) {
                        String decoded = decode(parts[3]);
                        appendMessage("[" + currentTime()
                                + "]  [Private] "
                                + parts[1] + " → " + parts[2]
                                + ": " + decoded);
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
            appendMessage("[" + timestamp + "]  "
                    + sender + ": " + message);
        } else {
            appendMessage("[" + timestamp + "]  [Private] "
                    + sender + " → " + receiver + ": " + message);
        }
    }

    private String decode(String encoded) {
        try {
            return new String(
                    Base64.getDecoder().decode(encoded),
                    StandardCharsets.UTF_8
            );
        } catch (IllegalArgumentException e) {
            return "[Unable to decode message]";
        }
    }

    private void updateUsers(String commaSeparatedUsers) {
        usersModel.clear();

        if (commaSeparatedUsers == null
                || commaSeparatedUsers.isBlank()) {
            return;
        }

        for (String user : commaSeparatedUsers.split(",")) {
            if (!user.isBlank()) {
                usersModel.addElement(user);
            }
        }
    }

    private void appendMessage(String message) {
        messageArea.append(message + "\n\n");
        messageArea.setCaretPosition(
                messageArea.getDocument().getLength()
        );
    }

    private void appendSystemMessage(String message) {
        appendMessage("• " + message);
    }

    private String currentTime() {
        return timeFormat.format(new Date());
    }
}
