package it.unifi.student.view;

import it.unifi.student.data.*;
import it.unifi.student.businesslogic.*;
import javax.swing.SwingUtilities;

/**
 * Punto di ingresso principale dell'applicazione.
 * Gestisce l'inizializzazione del database e l'iniezione delle dipendenze.
 */
public class App {
    public static void main(String[] args) {

        // 1. Inizializzazione Database 
        // Eseguiamo lo schema per creare le tabelle e il file default per popolare il catalogo
        System.out.println("LOG: Avvio inizializzazione database...");
        DatabaseManager.executeSqlScript("/sql/schema.sql");
        DatabaseManager.executeSqlScript("/sql/default.sql");

        // 2. Inizializzazione dei DAO (Pattern Singleton e JDBC)
        // Ora le istanze restituite lavorano direttamente su PostgreSQL
        ProdottoDAO pDao = ProdottoDAOImpl.getInstance();        
        OrdineDAO oDao = OrdineDAOImpl.getInstance();

        // 3. Creazione del Controller con Dependency Injection
        // Il controller riceve i DAO senza sapere che ora puntano a un DB reale
        AcquistoController controller = new AcquistoController(pDao, oDao);

        // 4. Configurazione Pattern Observer (Event-Driven)
        // Registriamo i servizi che devono reagire agli eventi di business
        controller.attach(new EmailService());
        controller.attach(new LogService());

        // 5. Avvio dell'Interfaccia Grafica
        System.out.println("LOG: Apertura interfaccia utente...");
        SwingUtilities.invokeLater(() -> {
            new LoginPage(controller).setVisible(true);
        });
    }
}