package it.unifi.student.businesslogic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unifi.student.data.CouponDAO;
import it.unifi.student.data.CouponDAOImpl;
import it.unifi.student.data.DatabaseManager;
import it.unifi.student.data.ProdottoDAO;
import it.unifi.student.data.ProdottoDAOImpl;
import it.unifi.student.domain.Prodotto;

public class CatalogoControllerTest {

    private CatalogoController controller;
    private ProdottoDAO pDao;
    private CouponDAO cDao;

    @BeforeEach
    void setUp() {
        DatabaseManager.executeSqlScript("/sql/reset.sql");
        DatabaseManager.executeSqlScript("/sql/schema.sql");

        pDao = ProdottoDAOImpl.getInstance();
        cDao = CouponDAOImpl.getInstance();
        controller = new CatalogoController(pDao, cDao);
    }

    @Test
    public void testAggiungiProdotto_Successo() {
        controller.aggiungiNuovoProdotto("P99", "Nuovo", 10.0);
        
        Prodotto p = pDao.getProdottoById("P99");
        assertNotNull(p);
        assertEquals("Nuovo", p.getNome());
    }

    @Test
    public void testAggiungiProdotto_Duplicato_LanciaEccezione() {
        controller.aggiungiNuovoProdotto("P01", "Primo", 10.0);
        
        assertThrows(IllegalArgumentException.class, () -> {
            controller.aggiungiNuovoProdotto("P01", "Secondo", 20.0);
        });
    }

    @Test
    public void testCreaCoupon_Successo() {
        controller.creaNuovoCoupon("TEST20", 20);
        assertNotNull(cDao.findByCodice("TEST20"));
    }
}
