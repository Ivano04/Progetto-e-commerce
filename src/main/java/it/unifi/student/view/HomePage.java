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
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import it.unifi.student.businesslogic.AcquistoController;
import it.unifi.student.businesslogic.CatalogoController; // NUOVO IMPORT
import it.unifi.student.businesslogic.UtenteController; // Opzionale, se vuoi gestire utenti qui
import it.unifi.student.domain.Prodotto;
import it.unifi.student.domain.Utente;

public class HomePage extends JFrame {

    private AcquistoController acquistoController;
    private CatalogoController catalogoController;
    private UtenteController utenteController; // Aggiungiamo anche questo per la gestione utenti admin
    private Utente utente;
    private JPanel gridPanel; 

    // Costruttore aggiornato: prende TUTTI e 3 i controller
    // (Nota: UtenteController serve se vuoi mantenere la funzionalità "Rimuovi Utenti" funzionante)
    public HomePage(AcquistoController acquistoController, CatalogoController catalogoController, UtenteController utenteController, Utente utente) {
        this.acquistoController = acquistoController;
        this.catalogoController = catalogoController;
        this.utenteController = utenteController;
        this.utente = utente;

        // Configurazione Finestra
        setTitle("UNIFI Shop - Catalogo");
        setSize(1000, 700); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 249, 250));

        // 1. Header (Titolo + Pannello Admin) 
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titolo = new JLabel("Benvenuto, " + utente.getNome());
        titolo.setFont(new Font("Arial", Font.BOLD, 24));
        titolo.setForeground(Color.WHITE);
        header.add(titolo, BorderLayout.WEST);

        // --- SEZIONE ADMIN ---
        if (utente.isAdmin()) {
            JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            adminPanel.setOpaque(false);

            // Bottone Gestione Utenti (Usa UtenteController)
            JButton btnAdmin = new JButton("🗑️ UTENTI");
            btnAdmin.setBackground(Color.RED);
            btnAdmin.setForeground(Color.WHITE);
            btnAdmin.setFocusPainted(false);
            btnAdmin.addActionListener(e -> {
                // Ora chiamo UtenteController
                List<Utente> listaUtenti = utenteController.getListaUtenti();
                String[] emails = new String[listaUtenti.size()];
                for (int i = 0; i < listaUtenti.size(); i++) {
                    emails[i] = listaUtenti.get(i).getEmail();
                }
                String selezione = (String) JOptionPane.showInputDialog(this, "Elimina utente:", "Admin", JOptionPane.WARNING_MESSAGE, null, emails, null);
                if (selezione != null && !selezione.equals(utente.getEmail())) {
                    utenteController.rimuoviUtente(selezione);
                    JOptionPane.showMessageDialog(this, "Utente eliminato.");
                }
            });

            // Bottone Aggiungi Prodotto (Usa CatalogoController)
            JButton btnAddProd = new JButton("➕ AGGIUNGI");
            btnAddProd.setBackground(new Color(46, 204, 113));
            btnAddProd.setForeground(Color.WHITE);
            btnAddProd.setFocusPainted(false);
            btnAddProd.addActionListener(e -> {
                String id = null;
                while (true) {
                    id = JOptionPane.showInputDialog(this, "Inserisci ID Prodotto (es. P09):");
                    if (id == null) return; 
                    if (id.trim().isEmpty()) continue; 
                    
                    // Verifica su CatalogoController
                    if (catalogoController.esisteProdotto(id)) {
                        JOptionPane.showMessageDialog(this, 
                            "L'ID '" + id + "' è già esistente!\nInseriscine uno nuovo.", 
                            "Errore ID Duplicato", 
                            JOptionPane.ERROR_MESSAGE);
                    } else {
                        break; 
                    }
                }

                String nome = JOptionPane.showInputDialog(this, "Nome Prodotto:");
                if (nome == null) return; 

                String prezzoStr = JOptionPane.showInputDialog(this, "Prezzo:");
                if (prezzoStr == null) return;

                try {
                    // Chiamata a CatalogoController
                    catalogoController.aggiungiNuovoProdotto(id, nome, Double.parseDouble(prezzoStr));
                    refreshCatalogo(); 
                    JOptionPane.showMessageDialog(this, "Prodotto aggiunto con successo!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Il prezzo deve essere un numero valido!", "Errore Formato", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Errore generico nell'aggiunta.");
                }
            });

            // Bottone Rimuovi Prodotto (Usa CatalogoController)
            JButton btnRemProd = new JButton("🗑️ RIMUOVI PROD.");
            btnRemProd.setBackground(new Color(230, 126, 34)); 
            btnRemProd.setForeground(Color.WHITE);
            btnRemProd.setFocusPainted(false);
            btnRemProd.addActionListener(e -> {
                List<Prodotto> catalogo = catalogoController.getCatalogoProdotti();
                String[] ids = catalogo.stream().map(Prodotto::getId).toArray(String[]::new);
                
                String selezione = (String) JOptionPane.showInputDialog(this, "Elimina prodotto:", "Admin", JOptionPane.WARNING_MESSAGE, null, ids, null);
                
                if (selezione != null) {
                    catalogoController.rimuoviProdotto(selezione);
                    refreshCatalogo();
                    JOptionPane.showMessageDialog(this, "Prodotto rimosso!");
                }
            });

            // Bottone Crea Coupon (Usa CatalogoController)
            JButton btnCreaCoupon = new JButton("🎫 CREA COUPON");
            btnCreaCoupon.setBackground(new Color(155, 89, 182)); 
            btnCreaCoupon.setForeground(Color.WHITE);
            btnCreaCoupon.setFocusPainted(false);
            btnCreaCoupon.addActionListener(e -> {
                JPanel panel = new JPanel(new GridLayout(0, 1));
                JTextField txtCodice = new JTextField();
                JTextField txtSconto = new JTextField();
                
                panel.add(new JLabel("Codice Coupon (es. SCONTO50):"));
                panel.add(txtCodice);
                panel.add(new JLabel("Percentuale Sconto (1-100):"));
                panel.add(txtSconto);

                int result = JOptionPane.showConfirmDialog(this, panel, "Crea Nuovo Coupon",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    try {
                        String codice = txtCodice.getText().trim();
                        int sconto = Integer.parseInt(txtSconto.getText().trim());
                        
                        catalogoController.creaNuovoCoupon(codice, sconto);
                        JOptionPane.showMessageDialog(this, "Coupon " + codice.toUpperCase() + " creato con successo!");
                        
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Inserisci un numero intero valido per lo sconto!", "Errore", JOptionPane.ERROR_MESSAGE);
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Errore generico: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // Bottone Rimuovi Coupon (Usa CatalogoController)
            JButton btnRemCoupon = new JButton("🗑️ RIMUOVI COUPON");
            btnRemCoupon.setBackground(new Color(192, 57, 43)); 
            btnRemCoupon.setForeground(Color.WHITE);
            btnRemCoupon.setFocusPainted(false);
            btnRemCoupon.addActionListener(e -> {
                List<String> listaCodici = catalogoController.getListaCodiciCoupon();
                
                if (listaCodici.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Non ci sono coupon attivi da rimuovere.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                String[] coupons = listaCodici.toArray(new String[0]);
                String selezione = (String) JOptionPane.showInputDialog(
                    this, "Seleziona il coupon da eliminare:", "Rimuovi Coupon", 
                    JOptionPane.WARNING_MESSAGE, null, coupons, null
                );
                
                if (selezione != null) {
                    catalogoController.rimuoviCoupon(selezione);
                    JOptionPane.showMessageDialog(this, "Coupon " + selezione + " eliminato definitivamente.");
                }
            });

            adminPanel.add(btnAddProd);
            adminPanel.add(btnRemProd);
            adminPanel.add(btnCreaCoupon); 
            adminPanel.add(btnRemCoupon);
            adminPanel.add(btnAdmin);

            header.add(adminPanel, BorderLayout.EAST);
        }
        
        add(header, BorderLayout.NORTH);

        // 2. Griglia Prodotti
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

        //  3. Footer (Cronologia + Carrello)
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JButton btnHistory = new JButton("Cronologia Ordini");
        btnHistory.setPreferredSize(new Dimension(160, 40));
        btnHistory.setFocusPainted(false);
        // La cronologia usa AcquistoController
        btnHistory.addActionListener(e -> new CronologiaPage(acquistoController, utente).setVisible(true));

        JButton btnCart = new JButton("Vai al Carrello 🛒");
        btnCart.setPreferredSize(new Dimension(160, 40));
        btnCart.setBackground(new Color(46, 204, 113));
        btnCart.setForeground(Color.WHITE);
        btnCart.setFont(new Font("Arial", Font.BOLD, 13));
        btnCart.setFocusPainted(false);
        // Il carrello usa AcquistoController
        btnCart.addActionListener(e -> new CarrelloPage(acquistoController, utente).setVisible(true));

        footer.add(btnHistory); 
        footer.add(btnCart);    
        add(footer, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    // Metodo helper per aggiornare la griglia
    private void refreshCatalogo() {
        gridPanel.removeAll();
        // Uso CatalogoController per ottenere la lista
        List<Prodotto> prodotti = catalogoController.getCatalogoProdotti();
        for (Prodotto p : prodotti) {
            gridPanel.add(new ProductPanel(p, prod -> {
                // L'azione di aggiunta al carrello usa AcquistoController
                acquistoController.aggiungiAlCarrello(prod);
                JOptionPane.showMessageDialog(this, "Aggiunto al carrello!");
            }));
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }
}