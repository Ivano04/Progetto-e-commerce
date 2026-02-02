package it.unifi.student.view;

import it.unifi.student.domain.Prodotto;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class ProductPanel extends JPanel {

    public ProductPanel(Prodotto p, Consumer<Prodotto> onAddAction) {
        // Layout e stile della "Card"
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Info Prodotto (Sinistra)
        JLabel lblInfo = new JLabel("<html><div style='width:150px;'>" +
            "<b style='font-size:12px;'>" + p.getNome() + "</b><br/>" +
            "<span style='color:gray;'>ID: " + p.getId() + "</span><br/>" +
            "<b style='color:#2c3e50;'>€" + String.format("%.2f", p.getPrezzo()) + "</b>" +
            "</div></html>");
        add(lblInfo, BorderLayout.CENTER);

        // Bottone Aggiungi (Destra)
        JButton btnAdd = new JButton("Aggiungi");
        btnAdd.setFocusPainted(false);
        btnAdd.setBackground(new Color(46, 204, 113)); // Verde Smeraldo
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 12));
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnAdd.addActionListener(e -> onAddAction.accept(p));

        add(btnAdd, BorderLayout.EAST);
        
        // Dimensione massima per evitare che si allarghi troppo
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
    }
}