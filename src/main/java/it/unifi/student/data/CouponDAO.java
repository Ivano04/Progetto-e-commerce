package it.unifi.student.data;
import it.unifi.student.domain.Coupon;

public interface CouponDAO {
    void save(Coupon coupon);
    Coupon findByCodice(String codice);
    // Opzionale: void delete(String codice);
}