package it.unifi.student.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unifi.student.domain.Utente;

/**
 * Implementazione JDBC del DAO per la gestione degli Utenti.
 * Utilizza PostgreSQL per la persistenza e il controllo delle credenziali.
 */
public class UtenteDAOImpl implements UtenteDAO {

    private static UtenteDAOImpl instance;

    // Costruttore privato per garantire l'unicità dell'istanza (Pattern Singleton)
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
                    return new Utente(
                        rs.getString("email"),
                        rs.getString("nome"),
                        rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("ERRORE: Problema durante l'autenticazione: " + email);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean register(String nome, String email, String password) {
        // Ora inseriamo esplicitamente tutti e tre i valori
        String query = "INSERT INTO Utente (nome, email, password) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, nome);     // Il nome che l'utente ha scritto
            pstmt.setString(2, email);    // L'email
            pstmt.setString(3, password); // La password

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("ERRORE: Impossibile registrare l'utente: " + email);
            return false;
        }
    }
}