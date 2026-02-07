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
    private JPanel gridPanel; 

    public HomePage(AcquistoController controller, Utente utente) {
        this.controller = controller;
        this.utente = utente;

        // --- Configurazione Finestra ---
        setTitle("UNIFI Shop - Catalogo");
        setSize(1000, 700); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 249, 250));

        // --- 1. Header (Titolo + Pannello Admin) ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titolo = new JLabel("Benvenuto, " + utente.getNome());
        titolo.setFont(new Font("Arial", Font.BOLD, 24));
        titolo.setForeground(Color.WHITE);
        header.add(titolo, BorderLayout.WEST);

        // Sezione Admin
        if (utente.isAdmin()) {
            JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            adminPanel.setOpaque(false);

            // Bottone Gestione Utenti
            JButton btnAdmin = new JButton("🗑️ UTENTI");
            btnAdmin.setBackground(Color.RED);
            btnAdmin.setForeground(Color.WHITE);
            btnAdmin.setFocusPainted(false);
            btnAdmin.addActionListener(e -> {
                List<Utente> listaUtenti = controller.getListaUtenti();
                String[] emails = new String[listaUtenti.size()];
                for (int i = 0; i < listaUtenti.size(); i++) {
                    emails[i] = listaUtenti.get(i).getEmail();
                }
                String selezione = (String) JOptionPane.showInputDialog(this, "Elimina utente:", "Admin", JOptionPane.WARNING_MESSAGE, null, emails, null);
                if (selezione != null && !selezione.equals(utente.getEmail())) {
                    controller.rimuoviUtente(selezione);
                    JOptionPane.showMessageDialog(this, "Utente eliminato.");
                }
            });

            // Bottone Aggiungi Prodotto
            JButton btnAddProd = new JButton("➕ AGGIUNGI");
            btnAddProd.setBackground(new Color(46, 204, 113));
            btnAddProd.setForeground(Color.WHITE);
            btnAddProd.setFocusPainted(false);
            btnAddProd.addActionListener(e -> {
                String id = null;
                
                // 1. Ciclo per richiedere l'ID finché non è valido (univoco)
                while (true) {
                    id = JOptionPane.showInputDialog(this, "Inserisci ID Prodotto (es. P09):");
                    
                    if (id == null) return; // L'utente ha premuto Annulla
                    if (id.trim().isEmpty()) continue; // ID vuoto, richiedi
                    
                    // Controllo se esiste già usando il nuovo metodo del controller
                    if (controller.esisteProdotto(id)) {
                        JOptionPane.showMessageDialog(this, 
                            "L'ID '" + id + "' è già esistente!\nInseriscine uno nuovo.", 
                            "Errore ID Duplicato", 
                            JOptionPane.ERROR_MESSAGE);
                        // Il ciclo while ricomincia e richiede l'ID
                    } else {
                        break; // ID valido e libero, esco dal ciclo
                    }
                }

                // 2. Se siamo qui, l'ID è valido. Chiedo il resto dei dati.
                String nome = JOptionPane.showInputDialog(this, "Nome Prodotto:");
                if (nome == null) return; 

                String prezzoStr = JOptionPane.showInputDialog(this, "Prezzo:");
                if (prezzoStr == null) return;

                try {
                    controller.aggiungiNuovoProdotto(id, nome, Double.parseDouble(prezzoStr));
                    refreshCatalogo(); 
                    JOptionPane.showMessageDialog(this, "Prodotto aggiunto con successo!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Il prezzo deve essere un numero valido!", "Errore Formato", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    // Catch dell'eccezione lanciata dal controller (sicurezza extra)
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Errore generico nell'aggiunta.");
                }
            });

            // Bottone Rimuovi Prodotto
            JButton btnRemProd = new JButton("🗑️ RIMUOVI PROD.");
            btnRemProd.setBackground(new Color(230, 126, 34));
            btnRemProd.setForeground(Color.WHITE);
            btnRemProd.setFocusPainted(false);
            btnRemProd.addActionListener(e -> {
                List<Prodotto> catalogo = controller.getCatalogoProdotti();
                String[] ids = catalogo.stream().map(Prodotto::getId).toArray(String[]::new);
                
                String selezione = (String) JOptionPane.showInputDialog(this, "Elimina prodotto:", "Admin", JOptionPane.WARNING_MESSAGE, null, ids, null);
                
                if (selezione != null) {
                    controller.rimuoviProdotto(selezione);
                    refreshCatalogo();
                    JOptionPane.showMessageDialog(this, "Prodotto rimosso!");
                }
            });

            adminPanel.add(btnAddProd);
            adminPanel.add(btnRemProd);
            adminPanel.add(btnAdmin);
            header.add(adminPanel, BorderLayout.EAST);
        }
        
        add(header, BorderLayout.NORTH);

        // --- 2. Griglia Prodotti ---
        gridPanel = new JPanel(new GridLayout(0, 4, 20, 20));
        gridPanel.setBackground(new Color(248, 249, 250));
        gridPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        refreshCatalogo(); 

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(gridPanel, BorderLayout.NORTH);
        wrapper.setBackground(new Color(248, 249, 250));
        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. Footer (Cronologia + Carrello) ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        // Bottone Cronologia (REINSERITO)
        JButton btnHistory = new JButton("Cronologia Ordini");
        btnHistory.setPreferredSize(new Dimension(160, 40));
        btnHistory.setFocusPainted(false);
        btnHistory.addActionListener(e -> new CronologiaPage(controller, utente).setVisible(true));

        // Bottone Carrello
        JButton btnCart = new JButton("Vai al Carrello 🛒");
        btnCart.setPreferredSize(new Dimension(160, 40));
        btnCart.setBackground(new Color(46, 204, 113));
        btnCart.setForeground(Color.WHITE);
        btnCart.setFont(new Font("Arial", Font.BOLD, 13));
        btnCart.setFocusPainted(false);
        btnCart.addActionListener(e -> new CarrelloPage(controller, utente).setVisible(true));

        footer.add(btnHistory); // Aggiunto al pannello
        footer.add(btnCart);    // Aggiunto al pannello
        add(footer, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    // Metodo helper per aggiornare la griglia
    private void refreshCatalogo() {
        gridPanel.removeAll();
        List<Prodotto> prodotti = controller.getCatalogoProdotti();
        for (Prodotto p : prodotti) {
            gridPanel.add(new ProductPanel(p, prod -> {
                controller.aggiungiAlCarrello(prod);
                JOptionPane.showMessageDialog(this, "Aggiunto al carrello!");
            }));
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }
}