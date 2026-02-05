package it.unifi.student.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import it.unifi.student.domain.Utente;

/**
 * Implementazione JDBC del DAO per la gestione degli Utenti.
 * Utilizza PostgreSQL per la persistenza e il controllo delle credenziali.
 */
public class UtenteDAOImpl implements UtenteDAO {

    private static UtenteDAOImpl instance;

    // Costruttore pubblico/privato (Singleton)
    public UtenteDAOImpl() {}

    /**
     * Restituisce l'unica istanza della classe.
     */
    public static synchronized UtenteDAOImpl getInstance() {
        if (instance == null) {
            instance = new UtenteDAOImpl();
        }
        return instance;
    }

    /**
     * Ricerca un utente nel database tramite email e password.
     * Legge anche il campo 'is_admin' per capire i permessi.
     */
    @Override
    public Utente findByEmailAndPassword(String email, String password) {
        String query = "SELECT * FROM Utente WHERE email = ? AND password = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Costruiamo l'utente leggendo anche se è admin (4° parametro)
                    return new Utente(
                        rs.getString("email"),
                        rs.getString("nome"),
                        rs.getString("password"),
                        rs.getBoolean("is_admin") // <--- Importante: legge se è admin o no
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("ERRORE Login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Registra un nuovo utente.
     * Imposta di default is_admin = FALSE (i nuovi utenti non sono mai admin).
     */
    @Override
    public boolean register(String nome, String email, String password) {
        // Query aggiornata: inseriamo FALSE nella colonna is_admin
        String query = "INSERT INTO Utente (nome, email, password, is_admin) VALUES (?, ?, ?, FALSE)";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, nome);     // Nome inserito
            pstmt.setString(2, email);    // Email inserita
            pstmt.setString(3, password); // Password inserita
            // Il quarto valore è FALSE fisso (scritto nella query sopra)

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("ERRORE Registrazione: Impossibile registrare l'utente " + email);
            // e.printStackTrace(); // Decommenta se vuoi vedere l'errore SQL completo
            return false;
        }
    }
    @Override
    public List<Utente> findAll() {
        List<Utente> utenti = new ArrayList<>();
        String query = "SELECT * FROM Utente";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                // Usiamo il costruttore a 4 parametri leggendo is_admin
                utenti.add(new Utente(
                    rs.getString("email"),
                    rs.getString("nome"),
                    rs.getString("password"),
                    rs.getBoolean("is_admin")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utenti;
    }

    @Override
    public void deleteUtente(String email) {
        // NOTA: Se l'utente ha degli ordini, questa query fallirà a meno che
        // non abbiamo impostato ON DELETE CASCADE nel database.
        String query = "DELETE FROM Utente WHERE email = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, email);
            pstmt.executeUpdate();
            System.out.println("LOG: Utente eliminato: " + email);
            
        } catch (SQLException e) {
            System.err.println("ERRORE: Impossibile eliminare l'utente. Ha degli ordini attivi?");
            e.printStackTrace();
        }
    }
}