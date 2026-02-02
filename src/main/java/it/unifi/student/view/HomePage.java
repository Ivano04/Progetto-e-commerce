package it.unifi.student.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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

        // --- Configurazione Finestra (Più larga per 4 colonne) ---
        setTitle("UNIFI Shop - Catalogo");
        setSize(1000, 700); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 249, 250));

        // --- 1. Header Moderno ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titolo = new JLabel("Benvenuto, " + utente.getNome());
        titolo.setFont(new Font("Arial", Font.BOLD, 24));
        titolo.setForeground(Color.WHITE);
        header.add(titolo, BorderLayout.WEST);
        
        add(header, BorderLayout.NORTH);

        // --- 2. Griglia Prodotti (4 per riga) ---
        // GridLayout(righe, colonne, hgap, vgap). 0 righe significa "quante servono".
        JPanel gridPanel = new JPanel(new GridLayout(0, 4, 20, 20));
        gridPanel.setBackground(new Color(248, 249, 250));
        gridPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        List<Prodotto> prodotti = controller.getCatalogoProdotti();
        for (Prodotto p : prodotti) {
            gridPanel.add(new ProductPanel(p, prod -> {
                controller.aggiungiAlCarrello(prod);
                // Notifica discreta in basso anziché popup invasivo (opzionale)
                JOptionPane.showMessageDialog(this, prod.getNome() + " a carrello!");
            }));
        }

        // Wrapper per non far "esplodere" la griglia se ci sono pochi prodotti
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(gridPanel, BorderLayout.NORTH);
        wrapper.setBackground(new Color(248, 249, 250));

        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. Footer con Navigazione ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JButton btnHistory = new JButton("Cronologia Ordini");
        btnHistory.setPreferredSize(new Dimension(160, 40));
        btnHistory.addActionListener(e -> new CronologiaPage(controller, utente).setVisible(true));

        JButton btnCart = new JButton("Vai al Carrello 🛒");
        btnCart.setPreferredSize(new Dimension(160, 40));
        btnCart.setBackground(new Color(46, 204, 113));
        btnCart.setForeground(Color.WHITE);
        btnCart.setFont(new Font("Arial", Font.BOLD, 13));
        btnCart.addActionListener(e -> new CarrelloPage(controller, utente).setVisible(true));

        footer.add(btnHistory);
        footer.add(btnCart);
        add(footer, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }
}