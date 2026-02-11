package it.unifi.student.view;

import javax.swing.SwingUtilities;

import it.unifi.student.businesslogic.AcquistoController;
import it.unifi.student.businesslogic.CatalogoController; // NUOVO
import it.unifi.student.businesslogic.UtenteController;
import it.unifi.student.businesslogic.EmailService;
import it.unifi.student.businesslogic.LogService;
import it.unifi.student.data.CouponDAO;
import it.unifi.student.data.CouponDAOImpl;
import it.unifi.student.data.DatabaseManager; 
import it.unifi.student.data.OrdineDAO;
import it.unifi.student.data.OrdineDAOImpl;
import it.unifi.student.data.ProdottoDAO;
import it.unifi.student.data.ProdottoDAOImpl;
import it.unifi.student.data.UtenteDAO;
import it.unifi.student.data.UtenteDAOImpl;

public class App {
    public static void main(String[] args) {

        System.out.println("LOG: Avvio applicazione...");
        
        // PERSISTENZA
        //DatabaseManager.executeSqlScript("/sql/schema.sql");
        //DatabaseManager.executeSqlScript("/sql/default.sql");

        // 1. Inizializzazione dei DAO
        ProdottoDAO pDao = ProdottoDAOImpl.getInstance();        
        OrdineDAO oDao = OrdineDAOImpl.getInstance();
        UtenteDAO uDao = UtenteDAOImpl.getInstance();
        CouponDAO cDao = CouponDAOImpl.getInstance(); 

        // 2. Creazione dei TRE Controller
        UtenteController utenteController = new UtenteController(uDao);
        CatalogoController catalogoController = new CatalogoController(pDao, cDao);
        
        // AcquistoController ora è pulito e prende solo oDao e cDao
        AcquistoController acquistoController = new AcquistoController(oDao, cDao);

        // 3. Configurazione Observer
        acquistoController.attach(new EmailService());
        acquistoController.attach(new LogService());

        // 4. Avvio GUI
        System.out.println("LOG: Apertura interfaccia utente...");
        SwingUtilities.invokeLater(() -> {
            // Passiamo tutti i controller necessari alla LoginPage
            new LoginPage(utenteController, acquistoController, catalogoController).setVisible(true);
        });
    }
}