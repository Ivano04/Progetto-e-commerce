package it.unifi.student.data;

import it.unifi.student.domain.Prodotto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


//Implementazione JDBC del DAO per la gestione dei Prodotti.
//Utilizza PostgreSQL per la persistenza dei dati.

public class ProdottoDAOImpl implements ProdottoDAO {

    private static ProdottoDAOImpl instance;

    // Costruttore privato per pattern Singleton
    private ProdottoDAOImpl() {}

    
    //Restituisce l'unica istanza della classe.
    
    public static synchronized ProdottoDAOImpl getInstance() {
        if (instance == null) {
            instance = new ProdottoDAOImpl();
        }
        return instance;
    }

    
    //Recupera l'elenco completo dei prodotti dal database.
    //Implementa la funzione 'READ' dello schema CRUD.
     
    @Override
    public List<Prodotto> getAllProdotti() {
        List<Prodotto> catalogo = new ArrayList<>();
        String query = "SELECT * FROM Prodotto";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // Mapping: Trasformazione della riga del DB in oggetto Domain 
                catalogo.add(new Prodotto(
                    rs.getString("id"),
                    rs.getString("nome"),
                    rs.getDouble("prezzo")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero del catalogo prodotti");
            e.printStackTrace();
        }
        return catalogo;
    }

    
    //Ricerca un prodotto specifico tramite il suo identificativo univoco.
    //Utilizza PreparedStatement per prevenire SQL Injection.
    
    @Override
    public Prodotto getProdottoById(String id) {
        String query = "SELECT * FROM Prodotto WHERE id = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Prodotto(
                        rs.getString("id"),
                        rs.getString("nome"),
                        rs.getDouble("prezzo")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero del prodotto ID: " + id);
            e.printStackTrace();
        }
        return null;
    }
}