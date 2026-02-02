package it.unifi.student.view;

import javax.swing.*;
import java.awt.*;
import it.unifi.student.businesslogic.AcquistoController;
import it.unifi.student.domain.Prodotto;
import it.unifi.student.domain.Utente;

public class CarrelloPage extends JFrame {

    public CarrelloPage(AcquistoController controller, Utente utente) {

        setTitle("Carrello");
        setSize(400, 300);
        setLayout(new BorderLayout());

        JPanel lista = new JPanel(new GridLayout(0, 1));

        for (Prodotto p : controller.getCarrello()) {
            JPanel riga = new JPanel(new FlowLayout(FlowLayout.LEFT));
            riga.add(new JLabel(p.getNome() + " - €" + p.getPrezzo()));

            JButton remove = new JButton("Rimuovi");
            remove.addActionListener(e -> {
                controller.rimuoviDalCarrello(p);
                dispose();
                new CarrelloPage(controller, utente).setVisible(true);
            });

            riga.add(remove);
            lista.add(riga);
        }

        add(new JScrollPane(lista), BorderLayout.CENTER);

        JLabel totaleLbl = new JLabel(
                "Totale: €" + controller.getTotaleCarrello(),
                SwingConstants.CENTER
        );
        add(totaleLbl, BorderLayout.NORTH);

        JButton checkout = new JButton("Finalizza Acquisto");
        checkout.setBackground(Color.GREEN);

        checkout.addActionListener(e -> {
            if (controller.finalizzaAcquisto(utente) != null) {
                JOptionPane.showMessageDialog(this, "Acquisto completato!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Carrello vuoto!");
            }
        });

        add(checkout, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
    }
}
