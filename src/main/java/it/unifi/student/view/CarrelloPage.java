package it.unifi.student.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

import it.unifi.student.businesslogic.AcquistoController;
import it.unifi.student.businesslogic.NessunoScontoStrategy;
import it.unifi.student.businesslogic.ScontoPercentualeStrategy;
import it.unifi.student.domain.Prodotto;
import it.unifi.student.domain.Utente;

public class CarrelloPage extends JFrame {

    private AcquistoController controller;
    private Utente utente;
    private JPanel listaProdotti;
    private JLabel lblTotale;
    
    // Componenti per il coupon
    private JTextField txtCoupon;
    private JButton btnApplicaCoupon;

    public CarrelloPage(AcquistoController controller, Utente utente) {
        this.controller = controller;
        this.utente = utente;

        setTitle("Il tuo Carrello");
        setSize(500, 700); // Aumentato leggermente l'altezza per far stare il coupon
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 245, 245));

        // --- TITOLO ---
        JLabel titolo = new JLabel("Riepilogo Carrello", SwingConstants.CENTER);
        titolo.setFont(new Font("Arial", Font.BOLD, 20));
        titolo.setBorder(new EmptyBorder(20, 0, 20, 0));
        titolo.setForeground(new Color(44, 62, 80));
        add(titolo, BorderLayout.NORTH);

        // --- LISTA PRODOTTI (CENTRALE) ---
        listaProdotti = new JPanel();
        listaProdotti.setLayout(new BoxLayout(listaProdotti, BoxLayout.Y_AXIS));
        listaProdotti.setBackground(new Color(245, 245, 245));
        
        refreshCarrello(); // Popola la lista iniziale

        JScrollPane scrollPane = new JScrollPane(listaProdotti);
        scrollPane.setBorder(new EmptyBorder(0, 20, 0, 20));
        scrollPane.getViewport().setBackground(new Color(245, 245, 245));
        add(scrollPane, BorderLayout.CENTER);

        // --- PANNELLO SUD (COUPON + TOTALE + AZIONI) ---
        JPanel panelSud = new JPanel();
        panelSud.setLayout(new BoxLayout(panelSud, BoxLayout.Y_AXIS)); // Layout verticale
        panelSud.setBackground(Color.WHITE);
        panelSud.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            new EmptyBorder(10, 20, 10, 20)
        ));

        // 1. SEZIONE COUPON
        JPanel panelCoupon = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCoupon.setBackground(Color.WHITE);
        
        JLabel lblCoupon = new JLabel("Codice Sconto:");
        txtCoupon = new JTextField(10);
        btnApplicaCoupon = new JButton("Applica");
        
        // Stile bottone coupon
        btnApplicaCoupon.setBackground(new Color(52, 152, 219)); // Blu
        btnApplicaCoupon.setForeground(Color.WHITE);
        btnApplicaCoupon.setFocusPainted(false);
        
        // Azione del bottone Coupon
        btnApplicaCoupon.addActionListener(e -> gestisciCoupon());

        panelCoupon.add(lblCoupon);
        panelCoupon.add(txtCoupon);
        panelCoupon.add(btnApplicaCoupon);
        
        panelSud.add(panelCoupon);
        panelSud.add(Box.createRigidArea(new Dimension(0, 10))); // Spaziatore

        // 2. SEZIONE TOTALE E TASTI AZIONE
        JPanel panelTotaleAzioni = new JPanel(new BorderLayout());
        panelTotaleAzioni.setBackground(Color.WHITE);

        lblTotale = new JLabel();
        lblTotale.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotale.setForeground(new Color(44, 62, 80));
        updateTotaleDisplay(); // Inizializza il testo del totale
        
        panelTotaleAzioni.add(lblTotale, BorderLayout.WEST);

        JPanel panelAzioni = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelAzioni.setBackground(Color.WHITE);

        JButton btnAnnulla = new JButton("Chiudi");
        btnAnnulla.addActionListener(e -> dispose());

        JButton btnAcquista = new JButton("ACQUISTA");
        btnAcquista.setBackground(new Color(46, 204, 113)); // Verde
        btnAcquista.setForeground(Color.WHITE);
        btnAcquista.setFont(new Font("Arial", Font.BOLD, 13));
        btnAcquista.addActionListener(e -> gestisciAcquisto());

        panelAzioni.add(btnAnnulla);
        panelAzioni.add(btnAcquista);
        
        panelTotaleAzioni.add(panelAzioni, BorderLayout.EAST);
        
        panelSud.add(panelTotaleAzioni);

        add(panelSud, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
    }

    // --- METODI DI SUPPORTO ---

    private void gestisciCoupon() {
        String codice = txtCoupon.getText().trim().toUpperCase();
        
        if (codice.equals("ESTATE20")) {
            // Applica sconto 20%
            controller.setScontoStrategy(new ScontoPercentualeStrategy(20));
            JOptionPane.showMessageDialog(this, "Coupon 'ESTATE20' applicato! Sconto del 20%.");
            btnApplicaCoupon.setEnabled(false); // Disabilita per evitare doppi click
            txtCoupon.setEditable(false);
        } else if (codice.isEmpty()) {
            // Reset strategia
            controller.setScontoStrategy(new NessunoScontoStrategy());
            JOptionPane.showMessageDialog(this, "Nessun coupon inserito. Prezzo pieno ripristinato.");
             btnApplicaCoupon.setEnabled(true);
             txtCoupon.setEditable(true);
        } else {
            JOptionPane.showMessageDialog(this, "Codice non valido!", "Errore", JOptionPane.ERROR_MESSAGE);
            // Non resetto la strategia qui, lascio quella che c'era (o default)
        }
        updateTotaleDisplay(); // Ricalcola e aggiorna la Label
    }

    private void gestisciAcquisto() {
        if (controller.getCarrello().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Il carrello è vuoto!", "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (controller.finalizzaAcquisto(utente) != null) {
            JOptionPane.showMessageDialog(this, "Acquisto completato con successo!", "Grazie", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Chiude la finestra
        } else {
            JOptionPane.showMessageDialog(this, "Errore durante l'acquisto.", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshCarrello() {
        listaProdotti.removeAll();
        List<Prodotto> carrello = controller.getCarrello();

        if (carrello.isEmpty()) {
            JLabel vuoto = new JLabel("Il carrello è vuoto");
            vuoto.setAlignmentX(Component.CENTER_ALIGNMENT);
            vuoto.setBorder(new EmptyBorder(50, 0, 0, 0));
            listaProdotti.add(vuoto);
        } else {
            for (Prodotto p : carrello) {
                JPanel riga = creaRigaProdotto(p);
                listaProdotti.add(riga);
                listaProdotti.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
        listaProdotti.revalidate();
        listaProdotti.repaint();
    }

    private JPanel creaRigaProdotto(Prodotto p) {
        JPanel riga = new JPanel(new BorderLayout());
        riga.setBackground(Color.WHITE);
        riga.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            new EmptyBorder(10, 15, 10, 15)
        ));
        riga.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        riga.add(new JLabel(p.getNome()), BorderLayout.WEST);

        JPanel azioniRiga = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        azioniRiga.setBackground(Color.WHITE);
        
        JLabel lblPrezzo = new JLabel("€" + String.format("%.2f", p.getPrezzo()));
        
        JButton btnRimuovi = new JButton("X");
        btnRimuovi.setForeground(Color.RED);
        btnRimuovi.setBorderPainted(false);
        btnRimuovi.setContentAreaFilled(false);
        btnRimuovi.setFocusPainted(false);
        btnRimuovi.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnRimuovi.addActionListener(e -> {
            controller.rimuoviDalCarrello(p);
            refreshCarrello();
            updateTotaleDisplay();
        });

        azioniRiga.add(lblPrezzo);
        azioniRiga.add(btnRimuovi);
        riga.add(azioniRiga, BorderLayout.EAST);
        
        return riga;
    }

    private void updateTotaleDisplay() {
        double totale = controller.getTotaleCarrello();
        lblTotale.setText("Totale: € " + String.format("%.2f", totale));
    }
}