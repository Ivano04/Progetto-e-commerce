package it.unifi.student.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import it.unifi.student.domain.Ordine;
import it.unifi.student.domain.Prodotto;
import it.unifi.student.domain.Utente;

public class OrdineDAOImpl implements OrdineDAO {
    private static OrdineDAOImpl instance;

    private OrdineDAOImpl() {}

    public static synchronized OrdineDAOImpl getInstance() {
        if (instance == null) instance = new OrdineDAOImpl();
        return instance;
    }

    @Override
    public void save(Ordine o) {
        String queryOrdine = "INSERT INTO Ordine (cliente_email, totale, stato) VALUES (?, ?, ?) RETURNING id";
        String queryProdotti = "INSERT INTO Ordine_Prodotti (id_ordine, id_prodotto) VALUES (?, ?)";

        try (Connection conn = ConnectionManager.getInstance().getConnection()) {
            conn.setAutoCommit(false); 

            try (PreparedStatement stmtOrd = conn.prepareStatement(queryOrdine)) {
                stmtOrd.setString(1, o.getCliente().getEmail());
                stmtOrd.setDouble(2, o.getTotale());
                stmtOrd.setString(3, o.getStato());
                
                ResultSet rs = stmtOrd.executeQuery();
                if (rs.next()) {
                    int idGenerato = rs.getInt(1);
                    o.setId(idGenerato);

                    try (PreparedStatement stmtProd = conn.prepareStatement(queryProdotti)) {
                        for (Prodotto p : o.getProdotti()) {
                            stmtProd.setInt(1, idGenerato);
                            stmtProd.setString(2, p.getId());
                            stmtProd.addBatch();
                        }
                        stmtProd.executeBatch();
                    }
                }
                conn.commit(); 
                System.out.println("LOG: Ordine #" + o.getId() + " salvato correttamente.");
            } catch (SQLException e) {
                conn.rollback(); 
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Ordine> findAll() {
        List<Ordine> ordini = new ArrayList<>();
        
        // Query senza immagine
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

                if (ordineId != lastId) {
                    // Qui usiamo il costruttore Utente a 4 parametri (con is_admin = false per default nella lettura ordini)
                    // Se ti da errore qui, controlla se Utente ha ancora il campo boolean admin.
                    Utente u = new Utente(rs.getString("cliente_email"), "", "", false);
                    
                    currentOrdine = new Ordine();
                    currentOrdine.setId(ordineId);
                    currentOrdine.setCliente(u);
                    currentOrdine.setTotale(rs.getDouble("totale"));
                    currentOrdine.setStato(rs.getString("stato"));
                    currentOrdine.setProdotti(new ArrayList<>());
                    
                    ordini.add(currentOrdine);
                    lastId = ordineId;
                }

                if (currentOrdine != null) {
                    // Costruttore a 3 parametri (Corretto per la tua versione senza immagini)
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

    // ECCO IL METODO CHE MANCAVA!
    @Override
    public void clear() {
        // Implementazione vuota o di reset se necessaria
        // DatabaseManager.executeSqlScript("/sql/schema.sql"); // Usa con cautela
    }
}