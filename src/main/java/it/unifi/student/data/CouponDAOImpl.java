package it.unifi.student.data;

import it.unifi.student.domain.Coupon;
import java.sql.*;

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
}