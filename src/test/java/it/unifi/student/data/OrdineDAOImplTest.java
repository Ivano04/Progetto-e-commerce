package it.unifi.student.data;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import it.unifi.student.domain.Ordine;
import it.unifi.student.domain.Prodotto;
import it.unifi.student.domain.Utente;

public class OrdineDAOImplTest {

    private OrdineDAO ordineDAO;
    private UtenteDAO utenteDAO; 
    private ProdottoDAO prodottoDAO; // Necessario perché findAll usa una JOIN sui prodotti

    @BeforeEach
    void setUp() {
        // Pulizia completa
        DatabaseManager.executeSqlScript("/sql/reset.sql");
        DatabaseManager.executeSqlScript("/sql/schema.sql");
        
        ordineDAO = OrdineDAOImpl.getInstance();
        utenteDAO = UtenteDAOImpl.getInstance();
        prodottoDAO = ProdottoDAOImpl.getInstance();
    }

    @Test
    void testSaveAndFindAll() {
        // 1. Setup Utente
        Utente u = new Utente("test.save@unifi.it", "TestUser", "pwd", false);
        utenteDAO.register(u.getNome(), u.getEmail(), u.getPassword());

        // 2. Setup Prodotto (Fondamentale per la JOIN di findAll)
        Prodotto p = new Prodotto("PROD1", "Prodotto Test", 10.0);
        prodottoDAO.save(p);

        int sizeBefore = ordineDAO.findAll().size();

        // 3. Creazione Ordine COMPLETO
        Ordine o = new Ordine();
        o.setCliente(u);
        o.setTotale(100.0);
        o.setStato("NUOVO");
        o.getProdotti().add(p); // Aggiungiamo il prodotto all'ordine
        
        ordineDAO.save(o);

        List<Ordine> ordini = ordineDAO.findAll();
        assertEquals(sizeBefore + 1, ordini.size(), "Il numero di ordini dovrebbe aumentare di 1");
    }

    @Test
    void testRimozioneOrdine_PhysicalDelete() {
        // 1. Setup Dati
        Utente u = new Utente("test.delete@unifi.it", "DeleteUser", "pwd", false);
        utenteDAO.register(u.getNome(), u.getEmail(), u.getPassword());

        Prodotto p = new Prodotto("PROD2", "Prodotto Delete", 20.0);
        prodottoDAO.save(p);

        // 2. Creazione Ordine
        Ordine o = new Ordine();
        o.setCliente(u);
        o.setTotale(50.0);
        o.setStato("DA_RIMUOVERE");
        o.getProdotti().add(p); // Collegamento prodotto
        
        ordineDAO.save(o);
        
        // 3. Verifica esistenza e Cancellazione
        List<Ordine> tutti = ordineDAO.findAll();
        assertFalse(tutti.isEmpty(), "La lista non dovrebbe essere vuota prima della cancellazione");
        
        int idGenerato = tutti.get(0).getId();
        ordineDAO.removeById(idGenerato);

        // 4. Verifica finale
        List<Ordine> dopoRimozione = ordineDAO.findAll();
        boolean ancoraPresente = dopoRimozione.stream().anyMatch(ord -> ord.getId() == idGenerato);
        assertFalse(ancoraPresente, "L'ordine rimosso è ancora presente nel DAO.");
    }
    
    @Test
    void testGenerazioneIdProgressivo() {
        // Setup base
        Utente u = new Utente("test.id@unifi.it", "IdUser", "pwd", false);
        utenteDAO.register(u.getNome(), u.getEmail(), u.getPassword());

        Prodotto p = new Prodotto("PROD3", "Prodotto ID", 5.0);
        prodottoDAO.save(p);

        // Ordine 1
        Ordine o1 = new Ordine();
        o1.setCliente(u);
        o1.setTotale(10.0);
        o1.getProdotti().add(p); // Importante!
        
        // Ordine 2
        Ordine o2 = new Ordine();
        o2.setCliente(u);
        o2.setTotale(20.0);
        o2.getProdotti().add(p); // Possiamo usare lo stesso prodotto
        
        ordineDAO.save(o1);
        ordineDAO.save(o2);

        // Verifica
        List<Ordine> salvati = ordineDAO.findAll();
        assertEquals(2, salvati.size());
        
        // Poiché l'ordine della lista è DESC (dal più recente), l'indice 0 è il secondo inserito
        Ordine ultimo = salvati.get(0);
        Ordine penultimo = salvati.get(1);

        assertTrue(ultimo.getId() > penultimo.getId(), "L'ID dell'ultimo ordine deve essere maggiore del precedente.");
    }
}