package it.unifi.student.data;

import it.unifi.student.domain.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione JDBC del DAO per la gestione degli Ordini.
 * Gestisce la persistenza complessa (relazione molti-a-molti) su PostgreSQL.
 */
public class OrdineDAOImpl implements OrdineDAO {
    private static OrdineDAOImpl instance;

    private OrdineDAOImpl() {}

    public static synchronized OrdineDAOImpl getInstance() {
        if (instance == null) instance = new OrdineDAOImpl();
        return instance;
    }

    /**
     * Salva un ordine nel database gestendo la transazione atomica.
     */
    @Override
    public void save(Ordine o) {
        String queryOrdine = "INSERT INTO Ordine (cliente_email, totale, stato) VALUES (?, ?, ?) RETURNING id";
        String queryProdotti = "INSERT INTO Ordine_Prodotti (id_ordine, id_prodotto) VALUES (?, ?)";

        try (Connection conn = ConnectionManager.getInstance().getConnection()) {
            conn.setAutoCommit(false); // Inizio transazione per garantire integrità

            try (PreparedStatement stmtOrd = conn.prepareStatement(queryOrdine)) {
                stmtOrd.setString(1, o.getCliente().getEmail());
                stmtOrd.setDouble(2, o.getTotale());
                stmtOrd.setString(3, o.getStato());
                
                ResultSet rs = stmtOrd.executeQuery();
                if (rs.next()) {
                    int idGenerato = rs.getInt(1);
                    o.setId(idGenerato);

                    // Salvataggio dei prodotti legati all'ordine tramite batch processing
                    try (PreparedStatement stmtProd = conn.prepareStatement(queryProdotti)) {
                        for (Prodotto p : o.getProdotti()) {
                            stmtProd.setInt(1, idGenerato);
                            stmtProd.setString(2, p.getId());
                            stmtProd.addBatch();
                        }
                        stmtProd.executeBatch();
                    }
                }
                conn.commit(); // Conferma definitiva dei dati
                System.out.println("LOG: Ordine #" + o.getId() + " salvato correttamente.");
            } catch (SQLException e) {
                conn.rollback(); // Annulla tutto in caso di errore
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recupera tutti gli ordini dal database ricostruendo la relazione con i prodotti.
     */
    @Override
    public List<Ordine> findAll() {
        List<Ordine> ordini = new ArrayList<>();
        // Query con JOIN per ottenere testata ordine e dettagli prodotti in un colpo solo
        String query = "SELECT o.id as ordine_id, o.totale, o.stato, o.cliente_email, " +
                       "p.id as prodotto_id, p.nome as prodotto_nome, p.prezzo " +
                       "FROM Ordine o " +
                       "JOIN Ordine_Prodotti op ON o.id = op.id_ordine " +
                       "JOIN Prodotto p ON op.id_prodotto = p.id " +
                       "ORDER BY o.id DESC";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            Ordine currentOrdine = null;
            int lastId = -1;

            while (rs.next()) {
                int ordineId = rs.getInt("ordine_id");

                // Se cambiamo ID, stiamo leggendo un nuovo ordine
                if (ordineId != lastId) {
                    Utente u = new Utente(rs.getString("cliente_email"), "", "");
                    currentOrdine = new Ordine();
                    currentOrdine.setId(ordineId);
                    currentOrdine.setCliente(u);
                    currentOrdine.setTotale(rs.getDouble("totale"));
                    currentOrdine.setStato(rs.getString("stato"));
                    currentOrdine.setProdotti(new ArrayList<>());
                    
                    ordini.add(currentOrdine);
                    lastId = ordineId;
                }

                // Aggiungiamo il prodotto della riga corrente all'ordine attivo
                if (currentOrdine != null) {
                    currentOrdine.getProdotti().add(new Prodotto(
                        rs.getString("prodotto_id"),
                        rs.getString("prodotto_nome"),
                        rs.getDouble("prezzo")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordini;
    }

    @Override
    public void removeById(int id) {
        String query = "DELETE FROM Ordine WHERE id = ?";
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clear() {
        // Reset totale per test di integrità
        DatabaseManager.executeSqlScript("/sql/reset.sql");
        DatabaseManager.executeSqlScript("/sql/schema.sql");
    }
}