package it.unifi.student.data;

import it.unifi.student.domain.Prodotto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdottoDAOImpl implements ProdottoDAO {

    private static ProdottoDAOImpl instance;

    private ProdottoDAOImpl() {}

    public static synchronized ProdottoDAOImpl getInstance() {
        if (instance == null) {
            instance = new ProdottoDAOImpl();
        }
        return instance;
    }

    @Override
    public List<Prodotto> getAllProdotti() {
        List<Prodotto> catalogo = new ArrayList<>();
        String query = "SELECT * FROM Prodotto";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
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
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void save(Prodotto p) {
        String query = "INSERT INTO Prodotto (id, nome, prezzo) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, p.getId());
            pstmt.setString(2, p.getNome());
            pstmt.setDouble(3, p.getPrezzo());
            pstmt.executeUpdate();
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
    }

    @Override
    public void delete(String id) {
        String query = "DELETE FROM Prodotto WHERE id = ?";
        try (Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
    }
}