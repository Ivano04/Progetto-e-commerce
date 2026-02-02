package it.unifi.student.businesslogic;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import it.unifi.student.domain.*;
import it.unifi.student.data.*;

/**
 * Test di unità per AcquistoController.
 * Verifica la logica di business e l'integrità del pattern Observer.
 */
public class AcquistoControllerTest {

    private AcquistoController controller;
    private StubObserver stubObserver;

    // --- Classe Interna per il Testing (Spy/Stub) ---
    // Utilizzata per monitorare le notifiche senza dipendere da servizi esterni (Email/Log)
    private class StubObserver implements Observer {
        public Ordine ordineRicevuto = null;
        public int chiamate = 0;

        @Override
        public void update(Ordine ordine) {
            this.ordineRicevuto = ordine;
            this.chiamate++;
        }
    }

    @BeforeEach
    void setUp() {
        // CORREZIONE: Usa l'implementazione reale ProdottoDAOImpl
        ProdottoDAO pDao = ProdottoDAOImpl.getInstance(); 
        OrdineDAO oDao = OrdineDAOImpl.getInstance();
        oDao.clear(); 

        controller = new AcquistoController(pDao, oDao);
        stubObserver = new StubObserver();
        controller.attach(stubObserver);
    }

    // Test Funzionali (Logica di Business)

    @Test
    public void testFinalizzaAcquisto_CalcoloTotale() {
        controller.aggiungiAlCarrello(new Prodotto("T1", "Prodotto Test 1", 10.0));
        controller.aggiungiAlCarrello(new Prodotto("T2", "Prodotto Test 2", 20.0));

        Utente u = new Utente("test@unifi.it", "Mario", "pass");
        Ordine result = controller.finalizzaAcquisto(u);

        assertNotNull(result);
        assertEquals(30.0, result.getTotale(), "Il totale calcolato non è corretto");
    }

    @Test
    public void testFinalizzaAcquisto_CarrelloVuoto() {
        Ordine result = controller.finalizzaAcquisto(new Utente());
        assertNull(result, "L'acquisto non dovrebbe essere possibile con un carrello vuoto");
    }

    @Test
    public void testAggiungiPerId_ProdottoEsistente() {
        controller.aggiungiPerId("P01"); // Prodotto pre-caricato nel ProdottoDAOImpl
        assertEquals(1, controller.getCarrello().size());
        assertEquals("Laptop Pro", controller.getCarrello().get(0).getNome());
    }

    // --- Test del Pattern Observer (Verifica delle Notifiche) ---

    @Test
    public void testObserverNotification_Successo() {
        // Simula il flusso base: aggiunta prodotto e checkout
        controller.aggiungiAlCarrello(new Prodotto("T1", "Prodotto Test", 50.0));
        Utente u = new Utente("test@unifi.it", "User", "pwd");
        
        Ordine result = controller.finalizzaAcquisto(u);

        // Verifica che la notifica sia stata inviata correttamente all'observer
        assertEquals(1, stubObserver.chiamate, "L'osservatore doveva essere notificato una volta");
        assertEquals(result, stubObserver.ordineRicevuto, "L'ordine passato all'osservatore non corrisponde");
    }

    @Test
    public void testObserverNotification_NessunaNotificaSuErrore() {
        // Simula il flusso alternativo: tentativo di acquisto con carrello vuoto
        controller.finalizzaAcquisto(new Utente());

        // Verifica che l'observer NON sia stato disturbato
        assertEquals(0, stubObserver.chiamate, "L'osservatore non deve essere notificato se l'acquisto fallisce");
    }
    // ... (mantenere gli import e la classe StubObserver come prima) ...

    @Test
    public void testCronologia_FiltroUtente() {
        Utente u1 = new Utente("mario@unifi.it", "Mario", "123");
        Utente u2 = new Utente("luigi@unifi.it", "Luigi", "456");

        controller.aggiungiAlCarrello(new Prodotto("P1", "Test", 10.0));
        controller.finalizzaAcquisto(u1);

        // Verifica che Mario veda il suo ordine e Luigi no
        assertEquals(1, controller.getCronologiaUtente(u1).size());
        assertTrue(controller.getCronologiaUtente(u2).isEmpty(), "Luigi non dovrebbe vedere l'ordine di Mario.");
    }

    @Test
    public void testCancellaOrdine_BusinessFlow() {
        Utente u = new Utente("mario@unifi.it", "Mario", "123");
        controller.aggiungiAlCarrello(new Prodotto("P1", "Test", 10.0));
        Ordine effettuato = controller.finalizzaAcquisto(u);
        
        int idDaCancellare = effettuato.getId();
        controller.cancellaOrdine(idDaCancellare);

        assertTrue(controller.getCronologiaUtente(u).isEmpty(), "La cronologia dovrebbe essere vuota dopo la cancellazione.");
    }

    @Test
    public void testCronologia_IntegritaDettagli() {
        Utente u = new Utente("mario@unifi.it", "Mario", "123");
        controller.aggiungiAlCarrello(new Prodotto("P1", "Laptop", 1000.0));
        controller.finalizzaAcquisto(u);

        Ordine recuperato = controller.getCronologiaUtente(u).get(0);
        
        // Verifica che i dati per la GUI siano corretti
        assertEquals(1, recuperato.getProdotti().size());
        assertEquals(1000.0, recuperato.getTotale());
    }
}
