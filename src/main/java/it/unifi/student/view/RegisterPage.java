package it.unifi.student.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import it.unifi.student.businesslogic.AcquistoController;

public class RegisterPage extends JFrame {

    private AcquistoController controller;

    public RegisterPage(AcquistoController controller) {
        this.controller = controller;

        // Configurazione Finestra 
        setTitle("Registrazione - UNIFI E-Commerce");
        setSize(450, 600); // Un po' più alta per far stare 3 campi
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout()); 
        getContentPane().setBackground(new Color(236, 240, 241));

        // Card Centrale
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(30, 40, 30, 40)
        ));

        // Titoli
        JLabel lblTitolo = new JLabel("Registrati");
        lblTitolo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitolo.setForeground(new Color(44, 62, 80));
        lblTitolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSotto = new JLabel("Crea un nuovo account");
        lblSotto.setForeground(Color.GRAY);
        lblSotto.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSotto.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // 1. Nome Utente
        JTextField txtNome = new JTextField();
        txtNome.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtNome.setBorder(BorderFactory.createTitledBorder("Nome Utente"));

        // 2. Email
        JTextField txtEmail = new JTextField();
        txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtEmail.setBorder(BorderFactory.createTitledBorder("Email"));

        // 3. Password
        JPasswordField txtPass = new JPasswordField();
        txtPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtPass.setBorder(BorderFactory.createTitledBorder("Password"));

        // Bottoni
        JButton btnConfirm = new JButton("CONFERMA REGISTRAZIONE");
        btnConfirm.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnConfirm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnConfirm.setBackground(new Color(46, 204, 113)); 
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Arial", Font.BOLD, 14));
        btnConfirm.setFocusPainted(false);
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnBack = new JButton("Torna al Login");
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setForeground(new Color(52, 152, 219)); 
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Logica
        btnConfirm.addActionListener(e -> {
            String nome = txtNome.getText();       
            String email = txtEmail.getText();     
            String password = new String(txtPass.getPassword()); 

            if (nome.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tutti i campi sono obbligatori!", "Attenzione", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Chiamo il controller passando 3 parametri
            boolean esito = controller.registraUtente(nome, email, password);

            if (esito) {
                JOptionPane.showMessageDialog(this, "Registrazione avvenuta con successo!");
                this.dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "Errore: Email già in uso.", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnBack.addActionListener(e -> this.dispose());

        // Assemblaggio
        card.add(lblTitolo);
        card.add(lblSotto);
        
        card.add(txtNome); // Aggiunto
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        
        card.add(txtEmail);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        
        card.add(txtPass);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        
        card.add(btnConfirm);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(btnBack);

        add(card);
        setLocationRelativeTo(null);
    }
}