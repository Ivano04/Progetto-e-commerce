package it.unifi.student.view;

import it.unifi.student.data.*;
import it.unifi.student.businesslogic.*;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {

        ProdottoDAO pDao = ProdottoDAOImpl.getInstance();        
        OrdineDAO oDao = OrdineDAOImpl.getInstance();

        AcquistoController controller = new AcquistoController(pDao, oDao);

        controller.attach(new EmailService());
        controller.attach(new LogService());

        SwingUtilities.invokeLater(() -> {
            new LoginPage(controller).setVisible(true);
        });
    }
}
