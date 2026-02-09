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
import it.unifi.student.businesslogic.CredenzialiNonValideException;
import it.unifi.student.domain.Utente;

public class LoginPage extends JFrame {

    private AcquistoController controller;

    public LoginPage(AcquistoController controller) {
        this.controller = controller;

        // Configurazione Finestra
        setTitle("Accesso - UNIFI E-Commerce");
        setSize(450, 550); // Ho aumentato leggermente l'altezza per farci stare i due tasti
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout()); 
        getContentPane().setBackground(new Color(236, 240, 241)); 

        // 1. Creazione della "Login Card" 
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(30, 40, 30, 40)
        ));

        //  2. Elementi Grafici 
        JLabel lblTitolo = new JLabel("Login");
        lblTitolo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitolo.setForeground(new Color(44, 62, 80));
        lblTitolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSotto = new JLabel("Inserisci le tue credenziali");
        lblSotto.setForeground(Color.GRAY);
        lblSotto.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSotto.setBorder(new EmptyBorder(0, 0, 20, 0));

        JTextField txtEmail = new JTextField();
        txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtEmail.setBorder(BorderFactory.createTitledBorder("Email"));

        JPasswordField txtPass = new JPasswordField();
        txtPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtPass.setBorder(BorderFactory.createTitledBorder("Password"));

        // Bottone ACCEDI
        JButton btnLogin = new JButton("ACCEDI");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogin.setBackground(new Color(52, 152, 219)); 
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Bottone REGISTRATI 
        JButton btnRegister = new JButton("REGISTRATI");
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnRegister.setBackground(new Color(46, 204, 113)); // Verde
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 3. Logica dei Bottoni
        
        // Logica Login
        btnLogin.addActionListener(e -> {
            String email = txtEmail.getText();
            String password = new String(txtPass.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Inserisci email e password!", "Attenzione", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Utente utenteLoggato = controller.autentica(email, password);
                new HomePage(controller, utenteLoggato).setVisible(true);
                this.dispose(); 
            } catch (CredenzialiNonValideException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore di accesso", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Logica Registrazione 
        btnRegister.addActionListener(e -> {
            new RegisterPage(controller).setVisible(true);
            // Non chiudiamo la login (this.dispose()) così l'utente può tornare qui dopo la registrazione
        });

        //4. Assemblaggio Card 
        card.add(lblTitolo);
        card.add(lblSotto);
        card.add(txtEmail);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(txtPass);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Aggiungo il bottone LOGIN
        card.add(btnLogin);
        
        // Aggiungo uno spazietto e poi il bottone REGISTRAZIONE
        card.add(Box.createRigidArea(new Dimension(0, 10))); 
        card.add(btnRegister); 

        add(card);
        setLocationRelativeTo(null);
    }
}