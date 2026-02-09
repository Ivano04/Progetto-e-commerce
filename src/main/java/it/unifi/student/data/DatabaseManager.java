package it.unifi.student.data;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility per la gestione del database PostgreSQL.
 * Si occupa del caricamento e dell'esecuzione degli script SQL (schema, default, reset).
 */
public class DatabaseManager {

    /**
     * Legge un file SQL dalle risorse e lo esegue sul database.
     * Necessario per l'inizializzazione del sistema e per i test di integrità.
     * * @param resourcePath Percorso del file nelle risorse (es: "/sql/default.sql")
     */
    public static void executeSqlScript(String resourcePath) {
        // Carichiamo il file come stream dalle risorse del progetto (src/main/resources)
        try (InputStream is = DatabaseManager.class.getResourceAsStream(resourcePath)) {
            
            if (is == null) {
                System.err.println("ERRORE: File SQL non trovato nel percorso: " + resourcePath);
                return;
            }

            // Leggiamo l'intero contenuto del file
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            
            // Dividiamo lo script in singoli comandi separati dal punto e virgola
            String[] commands = content.split(";");

            try (Connection conn = ConnectionManager.getInstance().getConnection();
                 Statement stmt = conn.createStatement()) {
                
                System.out.println("LOG: Inizio esecuzione script -> " + resourcePath);
                
                for (String query : commands) {
                    String cleanQuery = query.trim();
                    // Eseguiamo solo comandi non vuoti
                    if (!cleanQuery.isEmpty()) {
                        stmt.execute(cleanQuery);
                    }
                }
                
                System.out.println("LOG: Script eseguito con successo.");

            } catch (SQLException e) {
                System.err.println("ERRORE SQL durante l'esecuzione dello script: " + resourcePath);
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("ERRORE GENERICO durante il caricamento dello script: " + resourcePath);
            e.printStackTrace();
        }
    }
}