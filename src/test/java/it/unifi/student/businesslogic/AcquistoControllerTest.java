package it.unifi.student.businesslogic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unifi.student.data.OrdineDAO;
import it.unifi.student.data.OrdineDAOImpl;
import it.unifi.student.data.ProdottoDAO;
import it.unifi.student.data.ProdottoDAOImpl;
import it.unifi.student.data.UtenteDAO;
import it.unifi.student.data.UtenteDAOImpl;
import it.unifi.student.domain.Ordine;
import it.unifi.student.domain.Prodotto;
import it.unifi.student.domain.Utente;

/**
 * Test di unità per AcquistoController.
 * Verifica la logica di business e l'integrità del pattern Observer Event-Driven.
 */
public class AcquistoControllerTest {

    private AcquistoController controller;
    private StubObserver stubObserver;

    // --- Stub per il Testing ---
    // Cattura le notifiche per verificare che il controller faccia il suo dovere
    private class StubObserver implements Observer {
        public TipoEvento ultimoEvento = null;
        public Object ultimiDati = null;
        public int chiamate = 0;

        @Override
        public void update(TipoEvento evento, Object data) {
            this.ultimoEvento = evento;
            this.ultimiDati = data;
            this.chiamate++;
        }
    }

    @BeforeEach
    void setUp() {
        // Uso delle implementazioni reali (Singleton) come previsto dal disciplinare
        ProdottoDAO pDao = ProdottoDAOImpl.getInstance(); 
        OrdineDAO oDao = OrdineDAOImpl.getInstance();
        UtenteDAO uDao = UtenteDAOImpl.getInstance();
        oDao.clear(); // Garantisce l'indipendenza dei test 

        controller = new AcquistoController(pDao, oDao, uDao);
        stubObserver = new StubObserver();
        controller.attach(stubObserver);
    }

    // --- Test del Pattern Observer (Il cuore del refactoring) ---

    @Test
    public void testObserver_NotificaAcquistoCompletato() {
        controller.aggiungiAlCarrello(new Prodotto("T1", "Prodotto Test", 50.0));
        Utente u = new Utente("test@unifi.it", "User", "pwd",false);
        
        Ordine result = controller.finalizzaAcquisto(u);

        // Verifichiamo che l'evento sia quello giusto e contenga l'ordine
        assertEquals(1, stubObserver.chiamate);
        assertEquals(TipoEvento.ACQUISTO_COMPLETATO, stubObserver.ultimoEvento);
        assertEquals(result, stubObserver.ultimiDati);
    }

    @Test
    public void testObserver_NotificaCancellazioneOrdine() {
        Utente u = new Utente("mario@unifi.it", "Mario", "123",false);
        controller.aggiungiAlCarrello(new Prodotto("P1", "Test", 10.0));
        Ordine effettuato = controller.finalizzaAcquisto(u);
        
        // Reset dello stub per isolare la prossima notifica
        stubObserver.chiamate = 0;
        
        controller.cancellaOrdine(effettuato.getId());

        // Verifichiamo che sia scattata la notifica di cancellazione
        assertEquals(1, stubObserver.chiamate);
        assertEquals(TipoEvento.ORDINE_CANCELLATO, stubObserver.ultimoEvento);
        assertEquals(effettuato, stubObserver.ultimiDati);
    }

    @Test
    public void testObserver_NessunaNotificaSeCarrelloVuoto() {
        controller.finalizzaAcquisto(new Utente());

        // Se l'acquisto fallisce, non deve partire alcuna notifica
        assertEquals(0, stubObserver.chiamate);
    }

    // --- Test Funzionali (Logica di Business) ---

    @Test
    public void testFinalizzaAcquisto_CalcoloTotale() {
        controller.aggiungiAlCarrello(new Prodotto("T1", "P1", 10.0));
        controller.aggiungiAlCarrello(new Prodotto("T2", "P2", 20.0));

        Ordine result = controller.finalizzaAcquisto(new Utente("a@b.it", "A", "p",false));

        assertNotNull(result);
        assertEquals(30.0, result.getTotale());
    }

    @Test
    public void testCronologia_FiltroPerUtente() {
        Utente u1 = new Utente("mario@unifi.it", "Mario", "123",false);
        Utente u2 = new Utente("luigi@unifi.it", "Luigi", "456",false);

        controller.aggiungiAlCarrello(new Prodotto("P1", "Test", 10.0));
        controller.finalizzaAcquisto(u1);

        assertEquals(1, controller.getCronologiaUtente(u1).size());
        assertTrue(controller.getCronologiaUtente(u2).isEmpty());
    }
    @Test
    public void testRimuoviDalCarrello_VerificaDiminuzione() {
        Prodotto p = new Prodotto("T1", "Test", 10.0);
        controller.aggiungiAlCarrello(p);
        assertEquals(1, controller.getCarrello().size());

        controller.rimuoviDalCarrello(p);
        
        assertTrue(controller.getCarrello().isEmpty(), "Il carrello dovrebbe essere vuoto dopo la rimozione");
        assertEquals(0.0, controller.getTotaleCarrello(), "Il totale dovrebbe tornare a 0");
    }
    //test che verifica se il sistema lancia l'eccezione giusta in caso un utente non registrato provi ad accedere
    @Test
    public void testAutentica_CredenzialiInesistenti_LanciaException() {
        // Setup: credenziali non presenti nel file default.sql
        String emailErrata = "non.esisto@studenti.unifi.it";
        String passwordErrata = "12345";

        // Verifica strutturale del lancio dell'eccezione [cite: 915]
        assertThrows(CredenzialiNonValideException.class, () -> {
            controller.autentica(emailErrata, passwordErrata);
        }, "Il controller doveva lanciare CredenzialiNonValideException");
    }
}