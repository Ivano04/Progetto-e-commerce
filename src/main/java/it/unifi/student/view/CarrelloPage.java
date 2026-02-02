package it.unifi.student.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import it.unifi.student.businesslogic.AcquistoController;
import it.unifi.student.domain.Prodotto;
import it.unifi.student.domain.Utente;

public class CarrelloPage extends JFrame {

    private AcquistoController controller;
    private Utente utente;
    private JPanel listaProdotti;
    private JLabel lblTotale;

    public CarrelloPage(AcquistoController controller, Utente utente) {
        this.controller = controller;
        this.utente = utente;

        // --- Configurazione Finestra ---
        setTitle("Il tuo Carrello");
        setSize(500, 600);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 245, 245));

        // --- 1. Header ---
        JLabel titolo = new JLabel("Riepilogo Carrello", SwingConstants.CENTER);
        titolo.setFont(new Font("Arial", Font.BOLD, 20));
        titolo.setBorder(new EmptyBorder(20, 0, 20, 0));
        titolo.setForeground(new Color(44, 62, 80));
        add(titolo, BorderLayout.NORTH);

        // --- 2. Lista Prodotti (Centro) ---
        listaProdotti = new JPanel();
        listaProdotti.setLayout(new BoxLayout(listaProdotti, BoxLayout.Y_AXIS));
        listaProdotti.setBackground(new Color(245, 245, 245));
        
        refreshCarrello(); // Popola la lista

        JScrollPane scrollPane = new JScrollPane(listaProdotti);
        scrollPane.setBorder(new EmptyBorder(0, 20, 0, 20));
        scrollPane.getViewport().setBackground(new Color(245, 245, 245));
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. Pannello Riepilogo e Azioni (Sud) ---
        JPanel panelSud = new JPanel(new BorderLayout());
        panelSud.setBackground(Color.WHITE);
        panelSud.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            new EmptyBorder(20, 30, 20, 30)
        ));

        // Totale
        double totale = controller.getCarrello().stream().mapToDouble(Prodotto::getPrezzo).sum();
        lblTotale = new JLabel("Totale: €" + String.format("%.2f", totale));
        lblTotale.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotale.setForeground(new Color(44, 62, 80));
        panelSud.add(lblTotale, BorderLayout.WEST);

        // Bottoni
        JPanel panelAzioni = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelAzioni.setBackground(Color.WHITE);

        JButton btnAnnulla = new JButton("Chiudi");
        btnAnnulla.addActionListener(e -> dispose());

        JButton btnAcquista = new JButton("ACQUISTA");
        btnAcquista.setBackground(new Color(46, 204, 113));
        btnAcquista.setForeground(Color.WHITE);
        btnAcquista.setFont(new Font("Arial", Font.BOLD, 13));
        btnAcquista.setFocusPainted(false);
        
        btnAcquista.addActionListener(e -> {
            if (controller.finalizzaAcquisto(utente) != null) {
                JOptionPane.showMessageDialog(this, "Acquisto completato con successo!", "Grazie!", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Il carrello è vuoto!", "Attenzione", JOptionPane.WARNING_MESSAGE);
            }
        });

        panelAzioni.add(btnAnnulla);
        panelAzioni.add(btnAcquista);
        panelSud.add(panelAzioni, BorderLayout.EAST);

        add(panelSud, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private void refreshCarrello() {
        listaProdotti.removeAll();
        List<Prodotto> carrello = controller.getCarrello();

        if (carrello.isEmpty()) {
            JLabel vuoto = new JLabel("Il carrello è vuoto", SwingConstants.CENTER);
            vuoto.setAlignmentX(Component.CENTER_ALIGNMENT);
            vuoto.setBorder(new EmptyBorder(50, 0, 0, 0));
            listaProdotti.add(vuoto);
        } else {
            for (Prodotto p : carrello) {
                JPanel riga = new JPanel(new BorderLayout());
                riga.setBackground(Color.WHITE);
                riga.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 230, 230)),
                    new EmptyBorder(10, 15, 10, 15)
                ));
                riga.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

                riga.add(new JLabel(p.getNome()), BorderLayout.WEST);
                riga.add(new JLabel("€" + String.format("%.2f", p.getPrezzo())), BorderLayout.EAST);

                listaProdotti.add(riga);
                listaProdotti.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
        listaProdotti.revalidate();
        listaProdotti.repaint();
    }
}