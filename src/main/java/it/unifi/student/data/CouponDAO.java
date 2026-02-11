package it.unifi.student.data;
import it.unifi.student.domain.Coupon;
import java.util.List;

public interface CouponDAO {
    void save(Coupon coupon);
    Coupon findByCodice(String codice);
    List<String> getAllCodici(); // Per il menu a tendina
    void delete(String codice);  // Per eliminare
}