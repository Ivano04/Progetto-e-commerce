package it.unifi.student.view;

import it.unifi.student.data.*;
import it.unifi.student.businesslogic.*;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        // 1. Inizializzazione Strato Data (Singleton) [cite: 1330]
        ProdottoDAO pDao = ProdottoDAOImpl.getInstance();
        OrdineDAO oDao = OrdineDAOImpl.getInstance();

        // 2. Inizializzazione Business Logic (Dependency Injection) [cite: 582]
        AcquistoController controller = new AcquistoController(pDao, oDao);

        // 3. Configurazione Pattern Observer 
        controller.attach(new EmailService());
        controller.attach(new LogService());

        // 4. Avvio dell'Interfaccia Grafica 
        SwingUtilities.invokeLater(() -> {
            HomePage home = new HomePage(controller);
            home.setVisible(true);
            System.out.println("SISTEMA: Interfaccia grafica pronta.");
        });
    }
}