package it.unifi.student.data;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unifi.student.domain.Prodotto;



public class ProdottoDAOImplTest {

    private ProdottoDAO prodottoDAO;

    @BeforeEach
    void setUp() {
        DatabaseManager.executeSqlScript("/sql/reset.sql");
        DatabaseManager.executeSqlScript("/sql/schema.sql");

        prodottoDAO = ProdottoDAOImpl.getInstance();
    }

    @Test
    void testGetAllProdotti_NotEmpty() {
        List<Prodotto> prodotti = prodottoDAO.getAllProdotti();

        assertNotNull(prodotti);
        assertTrue(prodotti.isEmpty(), "Il catalogo non dovrebbe essere vuoto");
    }

    @Test
    void testGetProdottoById_Esistente() {
        Prodotto p = prodottoDAO.getProdottoById("P01");

        assertNotNull(p);
        assertEquals("Laptop Pro", p.getNome());
    }

    @Test
    void testGetProdottoById_Inesistente() {
        Prodotto p = prodottoDAO.getProdottoById("XXX");

        assertNull(p, "Un ID inesistente deve restituire null");
    }
}

