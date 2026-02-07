package it.unifi.student.view;

import it.unifi.student.domain.Prodotto;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class ProductPanel extends JPanel {

    public ProductPanel(Prodotto p, Consumer<Prodotto> onAddAction) {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        
        // Bordo arrotondato e ombreggiatura simulata
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // 1. Info centrali (Nome, ID e Prezzo)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        //  NOME PRODOTTO
        JLabel lblNome = new JLabel(p.getNome());
        lblNome.setFont(new Font("Arial", Font.BOLD, 14));
        lblNome.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ID PRODOTTO 
        // Aggiungiamo l'ID sotto il nome, un po' più piccolo e grigio
        JLabel lblId = new JLabel("ID: " + p.getId());
        lblId.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Font monospaced per dare l'idea di codice
        lblId.setForeground(Color.GRAY);
        lblId.setAlignmentX(Component.CENTER_ALIGNMENT);
        // ---------------------------

        // PREZZO 
        JLabel lblPrezzo = new JLabel("€" + String.format("%.2f", p.getPrezzo()));
        lblPrezzo.setFont(new Font("Arial", Font.PLAIN, 16));
        lblPrezzo.setForeground(new Color(44, 62, 80));
        lblPrezzo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPrezzo.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Aggiunta componenti al pannello verticale
        infoPanel.add(lblNome);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Spaziatura piccola
        infoPanel.add(lblId); // Aggiunta della label ID
        infoPanel.add(lblPrezzo);
        
        add(infoPanel, BorderLayout.CENTER);

        // 2. Bottone in basso
        JButton btnAdd = new JButton("Aggiungi");
        btnAdd.setBackground(new Color(52, 152, 219));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> onAddAction.accept(p));
        
        add(btnAdd, BorderLayout.SOUTH);

        // Dimensione fissa per la box della griglia
        setPreferredSize(new Dimension(200, 260)); // Aumentata leggermente l'altezza per far stare l'ID
    }
}