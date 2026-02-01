package it.unifi.student.businesslogic;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import it.unifi.student.domain.*;
import it.unifi.student.data.*;

public class AcquistoControllerTest {

    private AcquistoController controller;

    @BeforeEach
    void setUp() {
        // Recupero istanze Singleton
        ProdottoDAO pDao = ProdottoDAOImpl.getInstance();
        OrdineDAO oDao = OrdineDAOImpl.getInstance();
        
        // Iniezione nel controller
        controller = new AcquistoController(pDao, oDao);
    }

    @Test
    public void testFinalizzaAcquisto_CalcoloTotale() {
        controller.aggiungiAlCarrello(new Prodotto("1", "Test1", 10.0));
        controller.aggiungiAlCarrello(new Prodotto("2", "Test2", 5.0));

        Utente u = new Utente("test@studenti.unifi.it", "Mario", "pass");
        Ordine result = controller.finalizzaAcquisto(u);

        assertNotNull(result);
        assertEquals(15.0, result.getTotale());
    }

    @Test
    public void testFinalizzaAcquisto_CarrelloVuoto() {
        assertNull(controller.finalizzaAcquisto(new Utente()));
    }

    @Test
    public void testAggiungiPerId_ProdottoEsistente() {
        controller.aggiungiPerId("P01");
        assertEquals(1, controller.getCarrello().size());
        assertEquals("Laptop Pro", controller.getCarrello().get(0).getNome());
    }
}