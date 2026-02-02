package it.unifi.student.data;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import it.unifi.student.domain.Prodotto;
import java.util.List;



public class ProdottoDAOImplTest {

    @Test
    void testGetAllProdotti_NotEmpty() {
        ProdottoDAO dao = ProdottoDAOImpl.getInstance();

        List<Prodotto> prodotti = dao.getAllProdotti();

        assertNotNull(prodotti);
        assertFalse(prodotti.isEmpty(), "Il catalogo non dovrebbe essere vuoto");
    }

    @Test
    void testGetProdottoById_Esistente() {
        ProdottoDAO dao = ProdottoDAOImpl.getInstance();

        Prodotto p = dao.getProdottoById("P01");

        assertNotNull(p);
        assertEquals("Laptop Pro", p.getNome());
    }

    @Test
    void testGetProdottoById_Inesistente() {
        ProdottoDAO dao = ProdottoDAOImpl.getInstance();

        Prodotto p = dao.getProdottoById("XXX");

        assertNull(p, "Un ID inesistente deve restituire null");
    }
}

