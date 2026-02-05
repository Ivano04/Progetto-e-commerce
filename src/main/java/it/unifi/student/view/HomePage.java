package it.unifi.student.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import it.unifi.student.businesslogic.AcquistoController;
import it.unifi.student.domain.Prodotto;
import it.unifi.student.domain.Utente;

public class HomePage extends JFrame {

    private AcquistoController controller;
    private Utente utente;

    // Costruttore
    public HomePage(AcquistoController controller, Utente utente) {
        this.controller = controller;
        this.utente = utente;

        // --- Configurazione Finestra ---
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

        // --- SEZIONE ADMIN (SPOSTATA QUI DENTRO IL COSTRUTTORE) ---
        if (utente.isAdmin()) {
            JButton btnAdmin = new JButton("🗑️ GESTIONE UTENTI");
            btnAdmin.setBackground(Color.RED);
            btnAdmin.setForeground(Color.WHITE);
            btnAdmin.setFocusPainted(false);
            
            btnAdmin.addActionListener(e -> {
                // 1. Chiediamo al controller la lista degli utenti
                List<Utente> listaUtenti = controller.getListaUtenti();
                
                // 2. Creiamo un array di stringhe con le email da mostrare
                String[] emails = new String[listaUtenti.size()];
                for (int i = 0; i < listaUtenti.size(); i++) {
                    emails[i] = listaUtenti.get(i).getEmail() + " (" + listaUtenti.get(i).getNome() + ")";
                }

                // 3. Mostriamo una finestrella per scegliere chi cancellare
                String selezione = (String) JOptionPane.showInputDialog(
                    this, 
                    "Seleziona l'utente da eliminare:",
                    "Pannello SuperAdmin",
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    emails,
                    emails.length > 0 ? emails[0] : null
                );

                // 4. Se ha selezionato qualcuno, procediamo
                if (selezione != null) {
                    String emailTarget = selezione.split(" ")[0]; // Prende solo l'email
                    
                    if (emailTarget.equals(utente.getEmail())) {
                        JOptionPane.showMessageDialog(this, "Non puoi cancellare te stesso!");
                    } else {
                        controller.rimuoviUtente(emailTarget);
                        JOptionPane.showMessageDialog(this, "Utente " + emailTarget + " eliminato!");
                    }
                }
            });

            // Aggiungiamo il bottone Admin all'EST (Destra) dell'Header
            header.add(btnAdmin, BorderLayout.EAST);
        }
        // ---------------------------------------------------------
        
        add(header, BorderLayout.NORTH);

        // --- 2. Griglia Prodotti (4 per riga) ---
        JPanel gridPanel = new JPanel(new GridLayout(0, 4, 20, 20));
        gridPanel.setBackground(new Color(248, 249, 250));
        gridPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        List<Prodotto> prodotti = controller.getCatalogoProdotti();
        for (Prodotto p : prodotti) {
            gridPanel.add(new ProductPanel(p, prod -> {
                controller.aggiungiAlCarrello(prod);
                JOptionPane.showMessageDialog(this, prod.getNome() + " aggiunto al carrello!");
            }));
        }

        // Wrapper per lo scroll
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