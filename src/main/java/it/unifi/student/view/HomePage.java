package it.unifi.student.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
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

import it.unifi.student.businesslogic.controllerlogic.AcquistoController;
import it.unifi.student.businesslogic.controllerlogic.CatalogoController;
import it.unifi.student.businesslogic.controllerlogic.UtenteController;
import it.unifi.student.domain.Prodotto;
import it.unifi.student.domain.Utente;

public class HomePage extends JFrame {

    private AcquistoController acquistoController;
    private CatalogoController catalogoController;
    private UtenteController utenteController;
    private Utente utente;
    private JPanel gridPanel; 

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

        // --- 1. HEADER (Alleggerito) ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Parte Sinistra: Benvenuto
        JLabel titolo = new JLabel("Benvenuto, " + utente.getNome());
        titolo.setFont(new Font("Arial", Font.BOLD, 24));
        titolo.setForeground(Color.WHITE);
        header.add(titolo, BorderLayout.WEST);

        // Parte Destra: Pannello Admin (SOLO SE ADMIN)
        if (utente.isAdmin()) {
            JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            adminPanel.setOpaque(false);

            // Bottone Gestione Utenti
            JButton btnAdmin = new JButton("🗑️ UTENTI");
            btnAdmin.setBackground(Color.RED);
            btnAdmin.setForeground(Color.WHITE);
            btnAdmin.setFocusPainted(false);
            btnAdmin.addActionListener(e -> {
                List<Utente> listaUtenti = utenteController.getListaUtenti();
                if (listaUtenti.isEmpty()) return;
                
                String[] emails = new String[listaUtenti.size()];
                for (int i = 0; i < listaUtenti.size(); i++) {
                    emails[i] = listaUtenti.get(i).getEmail();
                }
                String selezione = (String) JOptionPane.showInputDialog(this, "Elimina utente:", "Admin", JOptionPane.WARNING_MESSAGE, null, emails, null);
                if (selezione != null && !selezione.equals(utente.getEmail())) {
                    utenteController.rimuoviUtente(selezione);
                    JOptionPane.showMessageDialog(this, "Utente eliminato.");
                } else if (selezione != null && selezione.equals(utente.getEmail())) {
                    JOptionPane.showMessageDialog(this, "Non puoi eliminare te stesso!", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Bottone Aggiungi Prodotto
            JButton btnAddProd = new JButton("➕ AGGIUNGI");
            btnAddProd.setBackground(new Color(46, 204, 113));
            btnAddProd.setForeground(Color.WHITE);
            btnAddProd.setFocusPainted(false);
            btnAddProd.addActionListener(e -> gestisciAggiuntaProdotto());

            // Bottone Rimuovi Prodotto
            JButton btnRemProd = new JButton("🗑️ RIMUOVI PROD.");
            btnRemProd.setBackground(new Color(230, 126, 34)); 
            btnRemProd.setForeground(Color.WHITE);
            btnRemProd.setFocusPainted(false);
            btnRemProd.addActionListener(e -> gestisciRimozioneProdotto());

            // Bottone Crea Coupon
            JButton btnCreaCoupon = new JButton("🎫 CREA COUPON");
            btnCreaCoupon.setBackground(new Color(155, 89, 182)); 
            btnCreaCoupon.setForeground(Color.WHITE);
            btnCreaCoupon.setFocusPainted(false);
            btnCreaCoupon.addActionListener(e -> gestisciCreazioneCoupon());

            // Bottone Rimuovi Coupon
            JButton btnRemCoupon = new JButton("🗑️ RIMUOVI COUPON");
            btnRemCoupon.setBackground(new Color(192, 57, 43)); 
            btnRemCoupon.setForeground(Color.WHITE);
            btnRemCoupon.setFocusPainted(false);
            btnRemCoupon.addActionListener(e -> gestisciRimozioneCoupon());

            // Aggiungo i bottoni Admin
            adminPanel.add(btnAddProd);
            adminPanel.add(btnRemProd);
            adminPanel.add(btnCreaCoupon); 
            adminPanel.add(btnRemCoupon);
            adminPanel.add(btnAdmin);
            
            header.add(adminPanel, BorderLayout.EAST);
        }
        
        add(header, BorderLayout.NORTH);

        // --- 2. GRIGLIA PRODOTTI ---
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

        // --- 3. FOOTER (Nuovo Layout: Logout a sinistra, Azioni a destra) ---
        JPanel footer = new JPanel(new BorderLayout()); // Cambiato layout in BorderLayout
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            new EmptyBorder(15, 20, 15, 20) // Padding interno
        ));

        // --- TASTO LOGOUT (Posizionato a OVEST/SINISTRA) ---
        JButton btnLogout = new JButton("ESCI");
        btnLogout.setPreferredSize(new Dimension(100, 40));
        btnLogout.setBackground(new Color(149, 165, 166)); // Grigio
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setFont(new Font("Arial", Font.BOLD, 12));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                this, 
                "Sei sicuro di voler uscire?", 
                "Logout", 
                JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                this.acquistoController.svuotaCarrello();
                new LoginPage(this.utenteController, this.acquistoController, this.catalogoController).setVisible(true);
                this.dispose();
            }
        });

        // --- TASTI AZIONE (Posizionati a EST/DESTRA) ---
        JPanel rightFooterActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightFooterActions.setOpaque(false);

        JButton btnHistory = new JButton("Cronologia Ordini");
        btnHistory.setPreferredSize(new Dimension(160, 40));
        btnHistory.setFocusPainted(false);
        btnHistory.addActionListener(e -> new CronologiaPage(acquistoController, utente).setVisible(true));

        JButton btnCart = new JButton("Vai al Carrello 🛒");
        btnCart.setPreferredSize(new Dimension(160, 40));
        btnCart.setBackground(new Color(46, 204, 113));
        btnCart.setForeground(Color.WHITE);
        btnCart.setFont(new Font("Arial", Font.BOLD, 13));
        btnCart.setFocusPainted(false);
        btnCart.addActionListener(e -> new CarrelloPage(acquistoController, utente).setVisible(true));

        rightFooterActions.add(btnHistory);
        rightFooterActions.add(btnCart);

        // Assemblaggio Footer
        footer.add(btnLogout, BorderLayout.WEST);       // Logout a sinistra
        footer.add(rightFooterActions, BorderLayout.EAST); // Azioni a destra
        
        add(footer, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    // --- Metodi Helper ---

    private void refreshCatalogo() {
        gridPanel.removeAll();
        List<Prodotto> prodotti = catalogoController.getCatalogoProdotti();
        for (Prodotto p : prodotti) {
            gridPanel.add(new ProductPanel(p, prod -> {
                acquistoController.aggiungiAlCarrello(prod);
                JOptionPane.showMessageDialog(this, "Aggiunto al carrello!");
            }));
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void gestisciAggiuntaProdotto() {
        String id = null;
        while (true) {
            id = JOptionPane.showInputDialog(this, "Inserisci ID Prodotto (es. P09):");
            if (id == null) return; 
            if (id.trim().isEmpty()) continue; 
            if (catalogoController.esisteProdotto(id)) {
                JOptionPane.showMessageDialog(this, "ID già esistente!", "Errore", JOptionPane.ERROR_MESSAGE);
            } else { break; }
        }
        String nome = JOptionPane.showInputDialog(this, "Nome Prodotto:");
        if (nome == null) return; 
        String prezzoStr = JOptionPane.showInputDialog(this, "Prezzo:");
        if (prezzoStr == null) return;

        try {
            catalogoController.aggiungiNuovoProdotto(id, nome, Double.parseDouble(prezzoStr));
            refreshCatalogo(); 
            JOptionPane.showMessageDialog(this, "Prodotto aggiunto!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Errore: " + ex.getMessage());
        }
    }

    private void gestisciRimozioneProdotto() {
         List<Prodotto> catalogo = catalogoController.getCatalogoProdotti();
         String[] ids = catalogo.stream().map(Prodotto::getId).toArray(String[]::new);
         String selezione = (String) JOptionPane.showInputDialog(this, "Elimina:", "Admin", JOptionPane.WARNING_MESSAGE, null, ids, null);
         if (selezione != null) {
             catalogoController.rimuoviProdotto(selezione);
             refreshCatalogo();
             JOptionPane.showMessageDialog(this, "Rimosso!");
         }
    }

    private void gestisciCreazioneCoupon() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JTextField txtCodice = new JTextField();
        JTextField txtSconto = new JTextField();
        panel.add(new JLabel("Codice:")); panel.add(txtCodice);
        panel.add(new JLabel("Sconto %:")); panel.add(txtSconto);
        int result = JOptionPane.showConfirmDialog(this, panel, "Nuovo Coupon", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                catalogoController.creaNuovoCoupon(txtCodice.getText(), Integer.parseInt(txtSconto.getText()));
                JOptionPane.showMessageDialog(this, "Coupon creato!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Errore: " + ex.getMessage()); }
        }
    }

    private void gestisciRimozioneCoupon() {
        List<String> codici = catalogoController.getListaCodiciCoupon();
        if(codici.isEmpty()) { JOptionPane.showMessageDialog(this, "Nessun coupon."); return; }
        String selezione = (String) JOptionPane.showInputDialog(this, "Elimina:", "Admin", JOptionPane.WARNING_MESSAGE, null, codici.toArray(new String[0]), null);
        if (selezione != null) {
            catalogoController.rimuoviCoupon(selezione);
            JOptionPane.showMessageDialog(this, "Eliminato.");
        }
    }
}