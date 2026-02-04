package it.unifi.student.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import it.unifi.student.businesslogic.AcquistoController;
import it.unifi.student.businesslogic.CredenzialiNonValideException;
import it.unifi.student.domain.Utente;

/**
 * Pagina di accesso dell'e-commerce.
 * Gestisce l'interazione con l'utente per l'autenticazione e 
 * visualizza messaggi di errore in caso di credenziali non valide.
 */
public class LoginPage extends JFrame {

    private AcquistoController controller;

    public LoginPage(AcquistoController controller) {
        this.controller = controller;

        // --- Configurazione Finestra ---
        setTitle("Accesso - UNIFI E-Commerce");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout()); 
        getContentPane().setBackground(new Color(236, 240, 241)); 

        // --- 1. Creazione della "Login Card" ---
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(30, 40, 30, 40)
        ));

        // --- 2. Elementi Grafici ---
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

        JButton btnLogin = new JButton("ACCEDI");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogin.setBackground(new Color(52, 152, 219)); 
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // --- 3. Logica del Bottone con Gestione Eccezioni ---
        btnLogin.addActionListener(e -> {
            String email = txtEmail.getText();
            String password = new String(txtPass.getPassword());

            // Controllo preliminare per campi vuoti
            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Inserisci email e password!", 
                    "Campi mancanti", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Tenta l'autenticazione tramite il Controller 
                // Il metodo ora interroga il database PostgreSQL reale
                Utente utenteLoggato = controller.autentica(email, password);
                
                // Se non viene lanciata l'eccezione, l'accesso è autorizzato
                new HomePage(controller, utenteLoggato).setVisible(true);
                this.dispose(); 
                
            } catch (CredenzialiNonValideException ex) {
                // Cattura l'errore sollevato dalla Business Logic e mostra il messaggio 
                JOptionPane.showMessageDialog(this, 
                    ex.getMessage(), 
                    "Errore di accesso", JOptionPane.ERROR_MESSAGE);
            }
        });

        // --- 4. Assemblaggio Card ---
        card.add(lblTitolo);
        card.add(lblSotto);
        card.add(txtEmail);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(txtPass);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        card.add(btnLogin);

        add(card);
        setLocationRelativeTo(null);
    }
}