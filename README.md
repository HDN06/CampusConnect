# CampusChat 💬

A Java-based real-time chat application built using **Java Swing, Socket Programming, and MySQL**. CampusChat demonstrates client-server communication, object-oriented programming, multithreading, and database integration through a simple desktop messaging application.

## ✨ Features

- 🔐 User login and authentication
- 💬 Real-time public messaging
- 👥 Multiple connected users
- 🟢 Online user list
- 🔒 Private messaging
- 💾 Persistent message storage using MySQL
- 📜 Chat history retrieved from the database
- 🖥️ Retro terminal-inspired Java Swing interface
- 🌐 Client-server architecture
- 🧵 Multithreaded client handling

## 🛠️ Tech Stack

- **Java** — Core application logic
- **Java Swing** — Graphical user interface
- **Java Sockets** — Client-server communication
- **MySQL** — Persistent message storage
- **JDBC** — Database connectivity
- **Object-Oriented Programming** — Application design
- **Multithreading** — Handling multiple client connections

## 📁 Project Structure

```text
CampusConnect/
├── lib/
│   └── mysql-connector-j-26.10.0.jar
├── out/
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

Before running the application, make sure you have:

- Java JDK installed
- MySQL Server installed and running
- MySQL Connector/J JDBC driver in the `lib/` directory
- A terminal or IDE such as VS Code

## 🗄️ Database Setup

Create the database in MySQL:

```sql
CREATE DATABASE campuschat_db;
USE campuschat_db;
```

Create the `users` table:

```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

Create the `messages` table:

```sql
CREATE TABLE messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender VARCHAR(50) NOT NULL,
    receiver VARCHAR(50) DEFAULT NULL,
    message TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Database configuration:** Update the connection URL, username, and password in `DatabaseManager.java` to match your local MySQL configuration. Never commit real database credentials to GitHub.

## 🚀 How to Run

Run the following commands from the project root directory.

### 1. Compile the application

```bash
javac -cp "lib/*" -d out $(find src -name "*.java")
```

### 2. Start CampusChat

```bash
java -cp "out:lib/*" com.campuschat.Main
```

The application starts with the server on **port 5000** and opens the client login interface, according to the current application configuration.

> **Important:** Include `lib/*` in the classpath so Java can load the MySQL JDBC driver.

## 🔑 Demo Accounts

The current server configuration includes these demo accounts:

| Username | Password |
|---|---|
| `user1` | `pass123` |
| `user2` | `pass123` |

Open separate client windows and log in with different accounts to test messaging.

*Note: These demo accounts are currently configured in the server code. Database-backed user registration and authentication should be documented separately once implemented.*

## 🏗️ Architecture

```text
       Client 1                         Client 2
          │                                │
          ▼                                ▼
   ChatClientGUI                    ChatClientGUI
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
                      ▼
                   MySQL
               (campuschat_db)
```

## 🧠 OOP Concepts Demonstrated

- **Classes and Objects** — Representing clients, users, and server components
- **Encapsulation** — Organizing data and behavior within classes
- **Constructors** — Initializing application objects
- **Interfaces** — Using Java interfaces such as `Runnable`
- **Polymorphism** — Using interface implementations and method behavior
- **Exception Handling** — Handling networking and database errors
- **Multithreading** — Processing multiple client connections concurrently

## 🎯 Project Goal

CampusChat is an academic Object-Oriented Programming project that demonstrates how a desktop chat application can be built using Java Swing, socket programming, multithreading, and MySQL database integration.

The project provides practical experience with client-server architecture, real-time communication, and persistent data storage.

---

**Built with ☕ Java and 💚 curiosity.**