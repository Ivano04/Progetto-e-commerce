package it.unifi.student.data;

import it.unifi.student.domain.Utente;
import java.sql.*;

/**
 * Implementazione JDBC del DAO per la gestione degli Utenti.
 * Utilizza PostgreSQL per la persistenza e il controllo delle credenziali.
 * Segue lo standard di progettazione illustrato nel disciplinare[cite: 181].
 */
public class UtenteDAOImpl implements UtenteDAO {

    private static UtenteDAOImpl instance;

    // Costruttore privato per garantire l'unicità dell'istanza (Pattern Singleton)
    private UtenteDAOImpl() {}

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
     * Implementa la logica di autenticazione richiesta per la gestione degli errori[cite: 709, 712].
     * * @param email La chiave primaria dell'utente.
     * @param password La password per la verifica.
     * @return L'oggetto Utente se trovato, null altrimenti.
     */
    @Override
    public Utente findByEmailAndPassword(String email, String password) {
        // Query che implementa la funzione di 'READ' specifica per le credenziali [cite: 812]
        String query = "SELECT * FROM Utente WHERE email = ? AND password = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            // Uso di PreparedStatement per la sicurezza contro SQL Injection
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Mapping dei dati dal database all'oggetto di dominio [cite: 584]
                    return new Utente(
                        rs.getString("email"),
                        rs.getString("nome"),
                        rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("ERRORE: Problema durante l'autenticazione dell'utente con email: " + email);
            e.printStackTrace();
        }
        
        // Restituisce null se non trova corrispondenze (permetterà al Controller di lanciare l'eccezione)
        return null;
    }
}