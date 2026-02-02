package it.unifi.student.view;

import it.unifi.student.businesslogic.AcquistoController;
import it.unifi.student.domain.Ordine;
import it.unifi.student.domain.Prodotto;
import it.unifi.student.domain.Utente;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class CronologiaPage extends JFrame {

    private AcquistoController controller;
    private Utente utente;
    private JPanel listaContenitore;

    public CronologiaPage(AcquistoController controller, Utente utente) {
        this.controller = controller;
        this.utente = utente;

        // --- Configurazione Finestra ---
        setTitle("I Miei Ordini - " + utente.getNome());
        setSize(550, 650);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 245, 245));

        // --- 1. Header (NORD) ---
        JLabel titolo = new JLabel("Storico Acquisti", SwingConstants.CENTER);
        titolo.setFont(new Font("Arial", Font.BOLD, 22));
        titolo.setForeground(new Color(44, 62, 80));
        titolo.setBorder(new EmptyBorder(25, 0, 20, 0));
        add(titolo, BorderLayout.NORTH);

        // --- 2. Pannello Centrale con Scroll ---
        listaContenitore = new JPanel();
        listaContenitore.setLayout(new BoxLayout(listaContenitore, BoxLayout.Y_AXIS));
        listaContenitore.setBackground(new Color(245, 245, 245));
        listaContenitore.setBorder(new EmptyBorder(0, 25, 10, 25));

        refreshLista(); // Popola la vista

        JScrollPane scrollPane = new JScrollPane(listaContenitore);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Scroll più fluido
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. Bottone di Chiusura (SUD) ---
        JPanel panelSud = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSud.setBackground(Color.WHITE);
        panelSud.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(210, 210, 210)));
        
        JButton btnChiudi = new JButton("Torna alla Home");
        btnChiudi.setPreferredSize(new Dimension(200, 40));
        btnChiudi.addActionListener(e -> dispose());
        panelSud.add(btnChiudi);
        
        add(panelSud, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private void refreshLista() {
        listaContenitore.removeAll();
        List<Ordine> ordini = controller.getCronologiaUtente(utente);

        if (ordini.isEmpty()) {
            JLabel vuoto = new JLabel("Non hai ancora effettuato ordini.");
            vuoto.setAlignmentX(Component.CENTER_ALIGNMENT);
            vuoto.setBorder(new EmptyBorder(50, 0, 0, 0));
            vuoto.setForeground(Color.GRAY);
            listaContenitore.add(vuoto);
        } else {
            for (Ordine o : ordini) {
                // --- Creazione Card Ordine ---
                JPanel orderCard = new JPanel();
                orderCard.setLayout(new BorderLayout(10, 10));
                orderCard.setBackground(Color.WHITE);
                orderCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                    new EmptyBorder(15, 20, 15, 20)
                ));
                orderCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

                // Header dell'ordine (ID e Data simulata)
                JLabel lblOrderInfo = new JLabel("ORDINE #" + o.getId());
                lblOrderInfo.setFont(new Font("Arial", Font.BOLD, 14));
                lblOrderInfo.setForeground(new Color(52, 152, 219));
                orderCard.add(lblOrderInfo, BorderLayout.NORTH);

                // Centro: Lista prodotti dell'ordine
                StringBuilder prodottiHtml = new StringBuilder("<html><ul style='margin-left: 10px;'>");
                for (Prodotto p : o.getProdotti()) {
                    prodottiHtml.append("<li>").append(p.getNome()).append(" (€").append(p.getPrezzo()).append(")</li>");
                }
                prodottiHtml.append("</ul></html>");
                
                JLabel lblProdotti = new JLabel(prodottiHtml.toString());
                lblProdotti.setForeground(new Color(127, 140, 141));
                orderCard.add(lblProdotti, BorderLayout.CENTER);

                // Footer: Totale e Bottone Elimina
                JPanel footer = new JPanel(new BorderLayout());
                footer.setBackground(Color.WHITE);
                
                JLabel lblTotale = new JLabel("TOTALE: €" + String.format("%.2f", o.getTotale()));
                lblTotale.setFont(new Font("Arial", Font.BOLD, 14));
                
                JButton btnElimina = new JButton("Rimuovi");
                btnElimina.setForeground(new Color(231, 76, 60)); // Rosso
                btnElimina.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btnElimina.addActionListener(e -> {
                    controller.cancellaOrdine(o.getId());
                    refreshLista();
                });

                footer.add(lblTotale, BorderLayout.WEST);
                footer.add(btnElimina, BorderLayout.EAST);
                orderCard.add(footer, BorderLayout.SOUTH);

                listaContenitore.add(orderCard);
                listaContenitore.add(Box.createRigidArea(new Dimension(0, 15))); // Spazio tra le card
            }
        }
        listaContenitore.revalidate();
        listaContenitore.repaint();
    }
}