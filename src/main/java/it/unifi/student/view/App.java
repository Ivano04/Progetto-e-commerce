package it.unifi.student.view;

import javax.swing.SwingUtilities;

import it.unifi.student.businesslogic.AcquistoController;
import it.unifi.student.businesslogic.EmailService;
import it.unifi.student.businesslogic.LogService;
import it.unifi.student.data.OrdineDAO;
import it.unifi.student.data.OrdineDAOImpl;
import it.unifi.student.data.ProdottoDAO;
import it.unifi.student.data.ProdottoDAOImpl;
import it.unifi.student.data.UtenteDAO;
import it.unifi.student.data.UtenteDAOImpl;


public class App {
    public static void main(String[] args) {

        // 1. Inizializzazione Database 
        System.out.println("LOG: Avvio applicazione...");

        // 2. Inizializzazione dei DAO (Pattern Singleton e JDBC)
        ProdottoDAO pDao = ProdottoDAOImpl.getInstance();        
        OrdineDAO oDao = OrdineDAOImpl.getInstance();
        UtenteDAO uDao = UtenteDAOImpl.getInstance();

        // 3. Creazione del Controller con Dependency Injection
        AcquistoController controller = new AcquistoController(pDao, oDao, uDao);

        // 4. Configurazione Pattern Observer (Event-Driven)
        controller.attach(new EmailService());
        controller.attach(new LogService());

        // 5. Avvio dell'Interfaccia Grafica
        System.out.println("LOG: Apertura interfaccia utente...");
        SwingUtilities.invokeLater(() -> {
            new LoginPage(controller).setVisible(true);
        });
    }
}