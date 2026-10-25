# CampusConnect 💬

**A Java-Based Real-Time Chat Application**

CampusConnect (CampusChat) is a desktop messaging application developed using Java, Java Swing, Socket Programming, and MySQL. It enables users to communicate in real time through a client-server architecture while demonstrating Object-Oriented Programming (OOP), multithreading, networking, and database integration.

## ✨ Features

- 🔐 **User Login** — Authentication using predefined demo accounts.
- 💬 **Real-Time Messaging** — Send public messages to connected users.
- 🔒 **Private Messaging** — Send messages to a specific online user.
- 👥 **Multiple Users** — Supports multiple simultaneous client connections.
- 🟢 **Online User List** — View currently connected users.
- 💾 **Message Persistence** — Store messages in a MySQL database.
- 📜 **Chat History** — Retrieve previously saved messages.
- 🖥️ **Java Swing GUI** — Desktop interface with a retro terminal-inspired theme.
- 🧵 **Multithreading** — Handle individual client connections concurrently.

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java | Core application logic |
| Java Swing | Graphical user interface |
| Java Socket Programming | Client-server communication |
| MySQL | Persistent message storage |
| JDBC | Java database connectivity |
| MySQL Connector/J | MySQL JDBC driver |
| Multithreading | Concurrent client handling |
| OOP | Application structure and design |

## 📁 Project Structure

```text
CampusConnect/
├── lib/
│   └── mysql-connector-j-26.10.0.jar
├── out/                         # Compiled classes (generated)
└── src/
    └── com/campuschat/
        ├── Main.java
        ├── client/
        │   ├── ChatClient.java
        │   ├── ChatClientGUI.java
        │   └── LoginScreen.java
        ├── model/
        │   └── User.java
        └── server/
            ├── ChatServer.java
            ├── ClientHandler.java
            ├── DatabaseManager.java
            ├── DatabaseTest.java
            └── CRUDTest.java
```

## ⚙️ Prerequisites

Make sure you have the following installed:

- Java JDK
- MySQL Server
- MySQL Connector/J JDBC driver
- VS Code or another Java IDE

## 🗄️ Database Setup

### 1. Create the database

Open your MySQL terminal and run:

```sql
CREATE DATABASE IF NOT EXISTS campuschat_db;
USE campuschat_db;
```

### 2. Create the users table

```sql
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 3. Create the messages table

```sql
CREATE TABLE IF NOT EXISTS messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender VARCHAR(50) NOT NULL,
    receiver VARCHAR(50) DEFAULT NULL,
    message TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 4. Configure the database connection

Open `src/com/campuschat/server/DatabaseManager.java` and configure the JDBC connection URL, username, and password to match your local MySQL setup.

Example connection URL:

```java
jdbc:mysql://localhost:3306/campuschat_db
```

**Security:** Never commit real database passwords or other credentials to GitHub.

## 🚀 How to Run

Run these commands from the project root directory.

### 1. Navigate to the project

```bash
cd CampusConnect
```

If your terminal is already open in the project folder, skip this step.

### 2. Start MySQL

On macOS with Homebrew:

```bash
brew services start mysql
```

If MySQL is already running, you can skip this command.

### 3. Compile the Java source files

```bash
javac -cp "lib/*" -d out $(find src -name "*.java")
```

### 4. Launch CampusConnect

```bash
java -cp "out:lib/*" com.campuschat.Main
```

The application launches using the configured client/server startup flow. The server listens on port **5000**.

> **Important:** Include `lib/*` in the classpath so Java can load the MySQL JDBC driver. Running with only `-cp out` may cause a `No suitable driver found` error.

## 🔑 Demo Accounts

The current server configuration includes these demo accounts:

| Username | Password |
|---|---|
| `user1` | `pass123` |
| `user2` | `pass123` |

Open separate client windows and log in using different accounts to test messaging.

> **Note:** Authentication currently uses accounts predefined in the Java server. Although a `users` table exists in MySQL, database-backed user registration and authentication are not implemented in the current login flow.

## 🗃️ View the Database and Chat History

You can inspect saved messages directly from the VS Code terminal.

### 1. Connect to MySQL

```bash
mysql -u root -p
```

Enter your MySQL password when prompted.

If your MySQL installation uses a different database username, replace `root` with that username.

### 2. Select the database

Run these commands inside the MySQL prompt:

```sql
USE campuschat_db;
```

### 3. View all saved messages

```sql
SELECT
    id,
    sender,
    receiver,
    message,
    sent_at
FROM messages
ORDER BY sent_at ASC, id ASC;
```

This displays the stored message ID, sender, recipient, content, and timestamp.

### 4. View public messages

```sql
SELECT
    sender,
    message,
    sent_at
FROM messages
WHERE receiver IS NULL
ORDER BY sent_at ASC, id ASC;
```

Public messages have a `NULL` receiver.

### 5. View private messages

```sql
SELECT
    sender,
    receiver,
    message,
    sent_at
FROM messages
WHERE receiver IS NOT NULL
ORDER BY sent_at ASC, id ASC;
```

Private messages include both the sender and recipient.

### 6. View chat history associated with a user

Example for `user1`:

```sql
SELECT
    sender,
    receiver,
    message,
    sent_at
FROM messages
WHERE receiver IS NULL
   OR sender = 'user1'
   OR receiver = 'user1'
ORDER BY sent_at ASC, id ASC;
```

This retrieves all public messages and private messages where `user1` is either the sender or recipient.

### 7. View the database tables

```sql
SHOW TABLES;
```

To inspect the table structure:

```sql
DESCRIBE messages;
DESCRIBE users;
```

### 8. Exit MySQL

```sql
EXIT;
```

**Tip:** Send some messages through CampusConnect before running the queries. This lets you see actual records saved by the application.

## 🏗️ System Architecture

```text
       Client 1                         Client 2
          │                                │
          ▼                                ▼
   Java Swing GUI                   Java Swing GUI
          │                                │
          └───────────┬────────────────────┘
                      ▼
                  ChatServer
                      │
              ┌───────┴────────┐
              ▼                ▼
       ClientHandler      ClientHandler
              │                │
              └───────┬────────┘
                      ▼
               DatabaseManager
                      │
                 JDBC Driver
                      │
                      ▼
                    MySQL
                campuschat_db
```

### How it works

1. Users log in through the desktop interface.
2. The client communicates with the server using Java sockets.
3. The server validates the configured demo credentials and manages connected clients.
4. Public messages are broadcast to connected users.
5. Private messages are forwarded to the specified online recipient.
6. The database manager stores messages in MySQL.
7. Clients can request previously saved chat history from the database.

## 🧠 OOP Concepts Demonstrated

- **Classes and Objects** — Organizing the application into separate components.
- **Encapsulation** — Keeping related data and operations within classes.
- **Constructors** — Initializing client, server, and other objects.
- **Interfaces** — Using interfaces such as `Runnable` for client-handler threads.
- **Exception Handling** — Handling network and database errors.
- **Multithreading** — Managing multiple client connections concurrently.

## 📸 Screenshots

Add screenshots of your actual running application here.

Suggested screenshots:

- Login screen
- Main chat interface
- Two users exchanging public messages
- Private messaging
- MySQL database displaying saved messages and chat history

## 🎯 Project Objective

The objective of CampusConnect is to demonstrate how Java, Object-Oriented Programming, socket-based communication, multithreading, and relational database integration can be combined to build a functional desktop messaging application.

The project provides practical experience in Java application development, client-server architecture, and persistent data storage.

## 👨‍💻 Academic Project

**CampusConnect / CampusChat**  
Department of Computer Science and Engineering  
SCMS School of Engineering and Technology

**Built with Java ☕**
