package it.unifi.student.view;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import it.unifi.student.businesslogic.AcquistoController;
import it.unifi.student.domain.Prodotto;
import it.unifi.student.domain.Utente;

public class HomePage extends JFrame {

    private AcquistoController controller;
    private Utente utente;

    public HomePage(AcquistoController controller, Utente utente) {
        this.controller = controller;
        this.utente = utente;

        setTitle("Catalogo Prodotti - Benvenuto " + utente.getNome());
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titolo = new JLabel("Catalogo Prodotti", SwingConstants.CENTER);
        titolo.setFont(new Font("Arial", Font.BOLD, 18));
        add(titolo, BorderLayout.NORTH);

        JPanel lista = new JPanel(new GridLayout(0, 1));
        List<Prodotto> prodotti = controller.getCatalogoProdotti();

        for (Prodotto p : prodotti) {
            JPanel riga = new JPanel(new FlowLayout(FlowLayout.LEFT));
            riga.add(new JLabel(p.getNome() + " - €" + p.getPrezzo()));

            JButton addBtn = new JButton("Aggiungi");
            addBtn.addActionListener(e -> {
                controller.aggiungiAlCarrello(p);
                JOptionPane.showMessageDialog(this, "Prodotto aggiunto!");
            });

            riga.add(addBtn);
            lista.add(riga);
        }

        add(new JScrollPane(lista), BorderLayout.CENTER);

        // --- CORREZIONE: Creazione del pannello per raggruppare i bottoni ---
        JPanel panelBottoni = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Pulsante Carrello
        JButton carrelloBtn = new JButton("Vai al Carrello");
        carrelloBtn.addActionListener(e -> {
            new CarrelloPage(controller, utente).setVisible(true);
        });
        panelBottoni.add(carrelloBtn);

        // Pulsante Cronologia (I miei Acquisti)
        JButton btnHistory = new JButton("I miei Acquisti");
        btnHistory.addActionListener(e -> {
            // Usiamo 'utente' passato al costruttore per coerenza con la sessione
            new CronologiaPage(controller, utente).setVisible(true);
        });
        panelBottoni.add(btnHistory);

        // pannello contenente i due bottoni alla zona SOUTH
        add(panelBottoni, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }
}