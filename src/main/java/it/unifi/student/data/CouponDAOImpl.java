package it.unifi.student.data;

import it.unifi.student.domain.Coupon;
import java.sql.*;
import java.util.List;

public class CouponDAOImpl implements CouponDAO {

    private static CouponDAOImpl instance;

    private CouponDAOImpl() {}

    public static synchronized CouponDAOImpl getInstance() {
        if (instance == null) instance = new CouponDAOImpl();
        return instance;
    }

    @Override
    public void save(Coupon coupon) {
        String query = "INSERT INTO Coupon (codice, percentuale_sconto) VALUES (?, ?)";
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, coupon.getCodice());
            pstmt.setInt(2, coupon.getPercentualeSconto());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore salvataggio coupon: " + e.getMessage());
        }
    }

    @Override
    public Coupon findByCodice(String codice) {
        String query = "SELECT * FROM Coupon WHERE codice = ?";
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, codice);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Coupon(
                        rs.getString("codice"),
                        rs.getInt("percentuale_sconto")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public List<String> getAllCodici() {
        List<String> codici = new java.util.ArrayList<>();
        String query = "SELECT codice FROM Coupon";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                codici.add(rs.getString("codice"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return codici;
    }

    @Override
    public void delete(String codice) {
        String query = "DELETE FROM Coupon WHERE codice = ?";
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, codice);
            pstmt.executeUpdate();
            System.out.println("LOG: Coupon eliminato: " + codice);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}