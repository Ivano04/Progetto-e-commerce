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

        // 1. Info centrali (Nome e Prezzo)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        JLabel lblNome = new JLabel(p.getNome());
        lblNome.setFont(new Font("Arial", Font.BOLD, 14));
        lblNome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPrezzo = new JLabel("€" + String.format("%.2f", p.getPrezzo()));
        lblPrezzo.setFont(new Font("Arial", Font.PLAIN, 16));
        lblPrezzo.setForeground(new Color(44, 62, 80));
        lblPrezzo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPrezzo.setBorder(new EmptyBorder(10, 0, 10, 0));

        infoPanel.add(lblNome);
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
        setPreferredSize(new Dimension(200, 250));
    }
}