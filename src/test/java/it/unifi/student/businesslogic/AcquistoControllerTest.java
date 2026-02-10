package it.unifi.student.businesslogic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unifi.student.data.DatabaseManager; // Fondamentale per pulire il DB
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
 */
public class AcquistoControllerTest {

    private AcquistoController controller;
    private StubObserver stubObserver;
    
    // Riferimenti ai DAO per popolare il DB nei test
    private ProdottoDAO pDao;
    private UtenteDAO uDao;
    private OrdineDAO oDao;

    // --- Stub per il Testing ---
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
        // 1. PULIZIA E SETUP DATABASE REALE
        // È fondamentale resettare il DB prima di ogni test per evitare conflitti di ID o dati sporchi
        DatabaseManager.executeSqlScript("/sql/reset.sql");
        DatabaseManager.executeSqlScript("/sql/schema.sql");
        // Non carico default.sql per avere controllo totale sui dati di test
        
        // 2. Inizializzazione DAO
        pDao = ProdottoDAOImpl.getInstance(); 
        oDao = OrdineDAOImpl.getInstance();
        uDao = UtenteDAOImpl.getInstance();

        controller = new AcquistoController(pDao, oDao, uDao);
        stubObserver = new StubObserver();
        controller.attach(stubObserver);
    }

    @Test
    public void testObserver_NotificaAcquistoCompletato() {
        // Setup dati DB
        Prodotto p = new Prodotto("T1", "Prodotto Test", 50.0);
        pDao.save(p); // Salvo il prodotto nel DB
        
        Utente u = new Utente("test@unifi.it", "User", "pwd", false);
        uDao.register(u.getNome(), u.getEmail(), "pwd"); // Salvo l'utente nel DB
        
        // Azione
        controller.aggiungiAlCarrello(p);
        Ordine result = controller.finalizzaAcquisto(u);

        // Verifica
        assertEquals(1, stubObserver.chiamate);
        assertEquals(TipoEvento.ACQUISTO_COMPLETATO, stubObserver.ultimoEvento);
        assertEquals(result, stubObserver.ultimiDati);
    }

    @Test
    public void testObserver_NotificaCancellazioneOrdine() {
        // 1. Preparo i dati nel DB
        Utente u = new Utente("mario@unifi.it", "Mario", "123", false);
        uDao.register(u.getNome(), u.getEmail(), "123"); // REGISTRO L'UTENTE
        
        Prodotto p = new Prodotto("P1", "Test", 10.0);
        pDao.save(p); // SALVO IL PRODOTTO
        
        // 2. Creo l'ordine
        controller.aggiungiAlCarrello(p);
        Ordine effettuato = controller.finalizzaAcquisto(u);
        
        // Reset dello stub per isolare la prossima notifica
        stubObserver.chiamate = 0;
        
        // 3. Cancello l'ordine
        controller.cancellaOrdine(effettuato.getId());

        // 4. Verifico che ora la notifica arrivi (perché l'ordine esisteva davvero nel DB)
        assertEquals(1, stubObserver.chiamate);
        assertEquals(TipoEvento.ORDINE_CANCELLATO, stubObserver.ultimoEvento);
        // Nota: non confronto l'oggetto esatto 'effettuato' perché il DAO potrebbe restituirne una nuova istanza
        assertNotNull(stubObserver.ultimiDati); 
    }

    @Test
    public void testObserver_NessunaNotificaSeCarrelloVuoto() {
        controller.finalizzaAcquisto(new Utente());
        assertEquals(0, stubObserver.chiamate);
    }

    @Test
    public void testFinalizzaAcquisto_CalcoloTotale() {
        // Preparo DB
        Prodotto p1 = new Prodotto("T1", "P1", 10.0);
        Prodotto p2 = new Prodotto("T2", "P2", 20.0);
        pDao.save(p1);
        pDao.save(p2);
        
        Utente u = new Utente("a@b.it", "A", "p", false);
        uDao.register(u.getNome(), u.getEmail(), "p");

        // Azione
        controller.aggiungiAlCarrello(p1);
        controller.aggiungiAlCarrello(p2);

        Ordine result = controller.finalizzaAcquisto(u);

        assertNotNull(result);
        assertEquals(30.0, result.getTotale());
    }

    @Test
    public void testCronologia_FiltroPerUtente() {
        // 1. Registro DUE utenti diversi nel DB
        Utente u1 = new Utente("mario@unifi.it", "Mario", "123", false);
        uDao.register(u1.getNome(), u1.getEmail(), "123");
        
        Utente u2 = new Utente("luigi@unifi.it", "Luigi", "456", false);
        uDao.register(u2.getNome(), u2.getEmail(), "456");

        // 2. Salvo un prodotto
        Prodotto p = new Prodotto("P1", "Test", 10.0);
        pDao.save(p);

        // 3. Mario fa un acquisto
        controller.aggiungiAlCarrello(p);
        controller.finalizzaAcquisto(u1);

        // 4. Verifico che Mario ha 1 ordine e Luigi 0
        assertEquals(1, controller.getCronologiaUtente(u1).size());
        assertTrue(controller.getCronologiaUtente(u2).isEmpty());
    }

    @Test
    public void testRimuoviDalCarrello_VerificaDiminuzione() {
        // Qui lavoriamo solo in memoria (RAM), non serve il DB per il carrello
        Prodotto p = new Prodotto("T1", "Test", 10.0);
        // Però per coerenza, se il metodo getProdottoById venisse chiamato, servirebbe. 
        // Ma aggiungiAlCarrello prende l'oggetto diretto, quindi ok.
        
        controller.aggiungiAlCarrello(p);
        assertEquals(1, controller.getCarrello().size());

        controller.rimuoviDalCarrello(p);
        
        assertTrue(controller.getCarrello().isEmpty(), "Il carrello dovrebbe essere vuoto dopo la rimozione");
        assertEquals(0.0, controller.getTotaleCarrello(), "Il totale dovrebbe tornare a 0");
    }

    @Test
    public void testAutentica_CredenzialiInesistenti_LanciaException() {
        String emailErrata = "non.esisto@studenti.unifi.it";
        String passwordErrata = "12345";

        assertThrows(CredenzialiNonValideException.class, () -> {
            controller.autentica(emailErrata, passwordErrata);
        }, "Il controller doveva lanciare CredenzialiNonValideException");
    }

    @Test
    public void testStrategy_CambioStrategiaRuntime() {
        System.out.println("--- TEST STRATEGY PATTERN ---");

        // 1. SETUP: Creo un prodotto da 100€ e un utente
        Prodotto p = new Prodotto("LUX", "Prodotto Lusso", 100.0);
        pDao.save(p);
        
        Utente u = new Utente("strategy@test.it", "StrategyUser", "pwd", false);
        uDao.register(u.getNome(), u.getEmail(), "pwd");

        // 2. Aggiungo al carrello
        controller.aggiungiAlCarrello(p);

        // 3. VERIFICA 1: Senza strategia (Prezzo Pieno)
        // Di default il controller usa NessunoScontoStrategy
        double totalePieno = controller.getTotaleCarrello();
        System.out.println("Totale senza sconto: " + totalePieno);
        assertEquals(100.0, totalePieno, "Il totale iniziale deve essere prezzo pieno");

        // 4. AZIONE: Cambio strategia a Runtime (es. Sconto 20%)
        // Qui simuli l'utente che inserisce un codice sconto
        controller.setScontoStrategy(new ScontoPercentualeStrategy(20));
        System.out.println("Strategia impostata: Sconto 20%");

        // 5. VERIFICA 2: Il totale deve essere aggiornato immediatamente
        double totaleScontato = controller.getTotaleCarrello();
        System.out.println("Totale scontato: " + totaleScontato);
        assertEquals(80.0, totaleScontato, "Il totale deve essere ridotto del 20%");

        // 6. VERIFICA 3: Finalizzazione Acquisto
        // L'ordine salvato nel DB deve avere il prezzo scontato
        Ordine ordine = controller.finalizzaAcquisto(u);
        assertNotNull(ordine);
        assertEquals(80.0, ordine.getTotale(), "L'ordine salvato deve mantenere lo sconto");
        
        System.out.println("--- TEST PASSATO CORRETTAMENTE ---");
    }
}