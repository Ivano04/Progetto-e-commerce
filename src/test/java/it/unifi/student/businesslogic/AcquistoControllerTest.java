package it.unifi.student.businesslogic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unifi.student.businesslogic.controllerlogic.AcquistoController;
import it.unifi.student.businesslogic.desingpattern.Observer;
import it.unifi.student.businesslogic.desingpattern.ScontoPercentualeStrategy;
import it.unifi.student.businesslogic.desingpattern.TipoEvento;
import it.unifi.student.data.CouponDAO;
import it.unifi.student.data.CouponDAOImpl;
import it.unifi.student.data.DatabaseManager;
import it.unifi.student.data.OrdineDAO;
import it.unifi.student.data.OrdineDAOImpl;
import it.unifi.student.data.ProdottoDAO;
import it.unifi.student.data.ProdottoDAOImpl;
import it.unifi.student.data.UtenteDAO;
import it.unifi.student.data.UtenteDAOImpl;
import it.unifi.student.domain.Coupon;
import it.unifi.student.domain.Ordine;
import it.unifi.student.domain.Prodotto;
import it.unifi.student.domain.Utente;


/**
 * Test di unità per AcquistoController (Refattorizzato).
 */
public class AcquistoControllerTest {

    private AcquistoController controller;
    private StubObserver stubObserver;
    
    // DAO necessari per il setup dei dati di prova
    private ProdottoDAO pDao;
    private UtenteDAO uDao;
    private OrdineDAO oDao;
    private CouponDAO cDao;

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
        // 1. PULIZIA E SETUP DATABASE
        DatabaseManager.executeSqlScript("/sql/reset.sql");
        DatabaseManager.executeSqlScript("/sql/schema.sql");
        
        // 2. Inizializzazione DAO
        pDao = ProdottoDAOImpl.getInstance(); 
        oDao = OrdineDAOImpl.getInstance();
        uDao = UtenteDAOImpl.getInstance();
        cDao = CouponDAOImpl.getInstance();

        // 3. Creazione Controller (NUOVO COSTRUTTORE: Solo Ordine e Coupon DAO)
        controller = new AcquistoController(oDao, cDao);
        
        stubObserver = new StubObserver();
        controller.attach(stubObserver);
    }

    @Test
    public void testObserver_NotificaAcquistoCompletato() {
        // Setup dati DB (uso i DAO direttamente)
        Prodotto p = new Prodotto("T1", "Prodotto Test", 50.0);
        pDao.save(p); 
        
        Utente u = new Utente("test@unifi.it", "User", "pwd", false);
        uDao.register(u.getNome(), u.getEmail(), "pwd"); 
        
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
        Utente u = new Utente("mario@unifi.it", "Mario", "123", false);
        uDao.register(u.getNome(), u.getEmail(), "123"); 
        
        Prodotto p = new Prodotto("P1", "Test", 10.0);
        pDao.save(p); 
        
        controller.aggiungiAlCarrello(p);
        Ordine effettuato = controller.finalizzaAcquisto(u);
        
        stubObserver.chiamate = 0;
        
        controller.cancellaOrdine(effettuato.getId());

        assertEquals(1, stubObserver.chiamate);
        assertEquals(TipoEvento.ORDINE_CANCELLATO, stubObserver.ultimoEvento);
        assertNotNull(stubObserver.ultimiDati); 
    }

    @Test
    public void testObserver_NessunaNotificaSeCarrelloVuoto() {
        controller.finalizzaAcquisto(new Utente());
        assertEquals(0, stubObserver.chiamate);
    }

    @Test
    public void testFinalizzaAcquisto_CalcoloTotale() {
        Prodotto p1 = new Prodotto("T1", "P1", 10.0);
        Prodotto p2 = new Prodotto("T2", "P2", 20.0);
        pDao.save(p1);
        pDao.save(p2);
        
        Utente u = new Utente("a@b.it", "A", "p", false);
        uDao.register(u.getNome(), u.getEmail(), "p");

        controller.aggiungiAlCarrello(p1);
        controller.aggiungiAlCarrello(p2);

        Ordine result = controller.finalizzaAcquisto(u);

        assertNotNull(result);
        assertEquals(30.0, result.getTotale());
    }

    @Test
    public void testCronologia_FiltroPerUtente() {
        Utente u1 = new Utente("mario@unifi.it", "Mario", "123", false);
        uDao.register(u1.getNome(), u1.getEmail(), "123");
        
        Utente u2 = new Utente("luigi@unifi.it", "Luigi", "456", false);
        uDao.register(u2.getNome(), u2.getEmail(), "456");

        Prodotto p = new Prodotto("P1", "Test", 10.0);
        pDao.save(p);

        controller.aggiungiAlCarrello(p);
        controller.finalizzaAcquisto(u1);

        assertEquals(1, controller.getCronologiaUtente(u1).size());
        assertTrue(controller.getCronologiaUtente(u2).isEmpty());
    }

    @Test
    public void testRimuoviDalCarrello_VerificaDiminuzione() {
        Prodotto p = new Prodotto("T1", "Test", 10.0);
        // Non serve salvare nel DB per testare il carrello in memoria, 
        // ma è buona prassi avere oggetti consistenti.
        
        controller.aggiungiAlCarrello(p);
        assertEquals(1, controller.getCarrello().size());

        controller.rimuoviDalCarrello(p);
        
        assertTrue(controller.getCarrello().isEmpty());
        assertEquals(0.0, controller.getTotaleCarrello());
    }

    @Test
    public void testStrategy_CambioStrategiaRuntime() {
        // Test puramente in memoria per la strategia
        Prodotto p = new Prodotto("LUX", "Prodotto Lusso", 100.0);
        
        controller.aggiungiAlCarrello(p);

        double totalePieno = controller.getTotaleCarrello();
        assertEquals(100.0, totalePieno);

        controller.setScontoStrategy(new ScontoPercentualeStrategy(20));
        
        double totaleScontato = controller.getTotaleCarrello();
        assertEquals(80.0, totaleScontato);
    }

    @Test
    public void testCoupon_Integrazione() {
        // 1. SETUP: Creo un coupon nel DB direttamente tramite DAO
        // (Simulo che l'admin lo abbia creato)
        String codice = "SCONTO50";
        cDao.save(new Coupon(codice, 50));

        // 2. Aggiungo prodotto al carrello
        Prodotto p = new Prodotto("TEST_COUPON", "Test", 100.0);
        pDao.save(p);
        controller.aggiungiAlCarrello(p);

        // 3. AZIONE: Applico il coupon tramite Controller
        boolean applicato = controller.applicaCoupon(codice);
        assertTrue(applicato, "Il coupon deve essere accettato dal controller");

        // 4. VERIFICA
        assertEquals(50.0, controller.getTotaleCarrello(), "Il totale deve essere dimezzato");
    }
}