package it.unifi.student.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;

public class OrdineTest {

    @Test
    void testSetAndGetTotale() {
        Ordine o = new Ordine();
        o.setTotale(99.99);

        assertEquals(99.99, o.getTotale());
    }

    @Test
    void testProdottiInitialization() {
        Ordine o = new Ordine();
        List<Prodotto> prodotti = o.getProdotti();

        assertNotNull(prodotti);
        assertTrue(prodotti.isEmpty(), "La lista prodotti deve essere inizializzata");
    }
}

