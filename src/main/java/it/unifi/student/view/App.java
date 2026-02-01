package it.unifi.student.view;

import it.unifi.student.domain.*;
import it.unifi.student.data.*;
import it.unifi.student.businesslogic.*; // Importiamo il controller e la logica dell'Observer
import java.util.List;

public class App {
    public static void main(String[] args) {
        System.out.println("=== BENVENUTO NELL'E-COMMERCE UNIFI ===");

        // 1. Inizializzazione Strato Data (Pattern Singleton)
        // Recuperiamo le istanze uniche dei DAO per garantire coerenza dei dati
        ProdottoDAO prodottoDAO = ProdottoDAOImpl.getInstance();
        OrdineDAO ordineDAO = OrdineDAOImpl.getInstance();

        // 2. Inizializzazione Business Logic (Pattern Dependency Injection)
        // Iniettiamo i DAO nel controller per disaccoppiare logica e persistenza
        AcquistoController controller = new AcquistoController(prodottoDAO, ordineDAO);

        // 3. Configurazione Pattern Observer
        // Creiamo gli osservatori concreti che reagiranno alla finalizzazione dell'acquisto
        Observer emailService = new EmailService();
        Observer logService = new LogService();

        // Registriamo (attach) gli osservatori al controller (il nostro Subject)
        controller.attach(emailService);
        controller.attach(logService);
        
        System.out.println("LOG: Servizi di notifica (Email, Log) configurati correttamente.");

        // 4. Simulazione: Visualizzazione Catalogo
        System.out.println("\n--- Catalogo Prodotti ---");
        List<Prodotto> catalogo = prodottoDAO.getAllProdotti();
        for (Prodotto p : catalogo) {
            System.out.println("[" + p.getId() + "] " + p.getNome() + " - €" + p.getPrezzo());
        }

        // 5. Simulazione: Selezione prodotti (Esercita la gestione del Carrello)
        System.out.println("\n--- Operazioni Carrello ---");
        System.out.println("Aggiunta prodotto P01 e P03...");
        controller.aggiungiPerId("P01");
        controller.aggiungiPerId("P03");

        // 6. Simulazione: Checkout (Esercita la finalizzazione e la notifica Observer)
        System.out.println("\n--- Finalizzazione Acquisto ---");
        Utente cliente = new Utente("mario.rossi@stud.unifi.it", "Mario Rossi", "password123");
        
        // Al termine di questo metodo, il controller chiamerà automaticamente notifyObservers()
        Ordine ordineEffettuato = controller.finalizzaAcquisto(cliente);

        if (ordineEffettuato != null) {
            System.out.println("\nORDINE COMPLETATO CON SUCCESSO!");
            System.out.println("Cliente: " + ordineEffettuato.getCliente().getNome());
            System.out.println("Totale pagato: €" + ordineEffettuato.getTotale());
            System.out.println("Stato ordine: " + ordineEffettuato.getStato());
        } else {
            System.out.println("ERRORE: Impossibile completare l'acquisto (carrello vuoto).");
        }

        // 7. Verifica Persistenza
        System.out.println("\n--- Verifica Database Ordini ---");
        System.out.println("Ordini totali salvati nel DAO: " + ordineDAO.findAll().size());
    }
}