package it.unifi.student.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestisce la connessione al database PostgreSQL tramite pattern Singleton. 
 */
public class ConnectionManager {
    private static ConnectionManager instance;
    private Connection connection;

    private ConnectionManager() throws SQLException {
        // Parametri per PostgreSQL
        String url = "jdbc:postgresql://localhost:5432/ecommerce_db";
        String user = "postgres"; 
        String password = "Santanna04??"; 
        this.connection = DriverManager.getConnection(url, user, password);
    }

    public static synchronized ConnectionManager getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}