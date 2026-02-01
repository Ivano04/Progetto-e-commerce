package it.unifi.student.view;

import it.unifi.student.domain.*;
import it.unifi.student.data.*;
import it.unifi.student.businesslogic.AcquistoController;
import java.util.List;

public class App {
    public static void main(String[] args) {
        System.out.println("=== BENVENUTO NELL'E-COMMERCE UNIFI ===");

        // 1. Inizializzazione Strato Data (Pattern Singleton)
        // Recuperiamo le istanze uniche dei DAO
        ProdottoDAO prodottoDAO = ProdottoDAOImpl.getInstance();
        OrdineDAO ordineDAO = OrdineDAOImpl.getInstance();

        // 2. Inizializzazione Business Logic (Pattern Dependency Injection)
        // Iniettiamo i DAO nel controller
        AcquistoController controller = new AcquistoController(prodottoDAO, ordineDAO);

        // 3. Simulazione: Visualizzazione Catalogo
        System.out.println("\n--- Catalogo Prodotti ---");
        List<Prodotto> catalogo = prodottoDAO.getAllProdotti();
        for (Prodotto p : catalogo) {
            System.out.println("[" + p.getId() + "] " + p.getNome() + " - €" + p.getPrezzo());
        }

        // 4. Simulazione: Selezione prodotti (UC #2 - Gestione Carrello)
        System.out.println("\n--- Operazioni Carrello ---");
        System.out.println("Aggiunta prodotto P01 e P03...");
        controller.aggiungiPerId("P01");
        controller.aggiungiPerId("P03");

        // 5. Simulazione: Checkout (UC #3 - Finalizza Acquisto)
        System.out.println("\n--- Finalizzazione Acquisto ---");
        Utente cliente = new Utente("mario.rossi@stud.unifi.it", "Mario Rossi", "password123");
        
        Ordine ordineEffettuato = controller.finalizzaAcquisto(cliente);

        if (ordineEffettuato != null) {
            System.out.println("ORDINE COMPLETATO CON SUCCESSO!");
            System.out.println("Cliente: " + ordineEffettuato.getCliente().getNome());
            System.out.println("Totale pagato: €" + ordineEffettuato.getTotale());
            System.out.println("Stato ordine: " + ordineEffettuato.getStato());
            System.out.println("Numero prodotti acquistati: " + ordineEffettuato.getProdotti().size());
        } else {
            System.out.println("ERRORE: Impossibile completare l'acquisto (carrello vuoto).");
        }

        // 6. Verifica Persistenza (Verifichiamo se l'ordine è nel DAO)
        System.out.println("\n--- Verifica Database Ordini ---");
        System.out.println("Ordini totali salvati: " + ordineDAO.findAll().size());
    }
}