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

        // --- Configurazione Finestra ---
        setTitle("UNIFI E-Commerce - Benvenuto " + utente.getNome());
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 245, 245)); // Grigio chiarissimo di sfondo

        // --- 1. Header (NORD) ---
        JLabel titolo = new JLabel("Catalogo Prodotti", SwingConstants.CENTER);
        titolo.setFont(new Font("Arial", Font.BOLD, 22));
        titolo.setBorder(new EmptyBorder(20, 0, 20, 0));
        titolo.setForeground(new Color(44, 62, 80)); // Blu notte
        add(titolo, BorderLayout.NORTH);

        // --- 2. Lista Prodotti con Scroll (CENTRO) ---
        JPanel listaContenitore = new JPanel();
        listaContenitore.setLayout(new BoxLayout(listaContenitore, BoxLayout.Y_AXIS));
        listaContenitore.setBackground(new Color(245, 245, 245));
        listaContenitore.setBorder(new EmptyBorder(10, 20, 10, 20));

        List<Prodotto> prodotti = controller.getCatalogoProdotti();

        for (Prodotto p : prodotti) {
            // Utilizziamo la classe ProductPanel per ogni "Card" prodotto
            ProductPanel card = new ProductPanel(p, prodottoSelezionato -> {
                controller.aggiungiAlCarrello(prodottoSelezionato);
                JOptionPane.showMessageDialog(this, 
                    prodottoSelezionato.getNome() + " aggiunto al carrello!", 
                    "Successo", JOptionPane.INFORMATION_MESSAGE);
            });
            
            listaContenitore.add(card);
            listaContenitore.add(Box.createRigidArea(new Dimension(0, 15))); // Spazio tra le card
        }

        JScrollPane scrollPane = new JScrollPane(listaContenitore);
        scrollPane.setBorder(null); // Rimuove il bordo brutto dello scroll
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. Pannello Bottoni (SUD) ---
        JPanel panelBottoni = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panelBottoni.setBackground(Color.WHITE);
        panelBottoni.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));

        // Bottone Carrello
        JButton carrelloBtn = new JButton("Vai al Carrello");
        carrelloBtn.setFont(new Font("Arial", Font.BOLD, 13));
        carrelloBtn.setPreferredSize(new Dimension(150, 40));
        carrelloBtn.addActionListener(e -> {
            new CarrelloPage(controller, utente).setVisible(true);
        });

        // Bottone Cronologia
        JButton btnHistory = new JButton("I miei Acquisti");
        btnHistory.setFont(new Font("Arial", Font.BOLD, 13));
        btnHistory.setPreferredSize(new Dimension(150, 40));
        btnHistory.addActionListener(e -> {
            new CronologiaPage(controller, utente).setVisible(true);
        });

        panelBottoni.add(carrelloBtn);
        panelBottoni.add(btnHistory);
        
        add(panelBottoni, BorderLayout.SOUTH);

        // Centra la finestra
        setLocationRelativeTo(null);
    }
}