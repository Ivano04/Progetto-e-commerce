package it.unifi.student.view;

import it.unifi.student.businesslogic.AcquistoController;
import it.unifi.student.domain.Prodotto;
import it.unifi.student.domain.Utente;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HomePage extends JFrame {
    private AcquistoController controller;

    public HomePage(AcquistoController controller) {
        this.controller = controller;

        // Configurazione base della finestra
        setTitle("E-Commerce Unifi - HomePage");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Pannello Superiore: Titolo
        JLabel lblTitolo = new JLabel("Catalogo Prodotti", SwingConstants.CENTER);
        lblTitolo.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitolo, BorderLayout.NORTH);

        // Pannello Centrale: Griglia prodotti (UC #1: Visualizza Catalogo)
        JPanel grid = new JPanel(new GridLayout(0, 1, 5, 5));
        List<Prodotto> prodotti = controller.getCatalogoProdotti();

        for (Prodotto p : prodotti) {
            JPanel productPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            productPanel.add(new JLabel(p.getNome() + " - €" + p.getPrezzo()));
            
            JButton btnAggiungi = new JButton("Aggiungi");
            // Gestione carrello (UC #2)
            btnAggiungi.addActionListener(e -> {
                controller.aggiungiAlCarrello(p);
                JOptionPane.showMessageDialog(this, p.getNome() + " aggiunto!");
            });
            
            productPanel.add(btnAggiungi);
            grid.add(productPanel);
        }
        add(new JScrollPane(grid), BorderLayout.CENTER);

        // Pannello Inferiore: Checkout (UC #3)
        JButton btnCheckout = new JButton("Finalizza Acquisto (Mario Rossi)");
        btnCheckout.setBackground(Color.GREEN);
        btnCheckout.addActionListener(e -> {
            Utente cliente = new Utente("mario.rossi@stud.unifi.it", "Mario Rossi", "pass");
            if (controller.finalizzaAcquisto(cliente) != null) {
                JOptionPane.showMessageDialog(this, "Acquisto completato con successo!");
            } else {
                JOptionPane.showMessageDialog(this, "Errore: il carrello è vuoto.", "Attenzione", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(btnCheckout, BorderLayout.SOUTH);
    }
}