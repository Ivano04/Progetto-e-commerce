package it.unifi.student.view;

import javax.swing.*;
import java.awt.*;
import it.unifi.student.businesslogic.AcquistoController;
import it.unifi.student.domain.Utente;

public class LoginPage extends JFrame {

    private AcquistoController controller;

    public LoginPage(AcquistoController controller) {
        this.controller = controller;

        setTitle("Login - E-Commerce UNIFI");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1, 5, 5));

        JTextField txtEmail = new JTextField();
        JTextField txtNome = new JTextField();
        JButton btnLogin = new JButton("Login");

        add(new JLabel("Email:"));
        add(txtEmail);
        add(new JLabel("Nome:"));
        add(txtNome);

        btnLogin.addActionListener(e -> {
            if (txtEmail.getText().isEmpty() || txtNome.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Compila tutti i campi!");
                return;
            }

            Utente utente = new Utente(
                    txtEmail.getText(),
                    txtNome.getText(),
                    "password"
            );

            HomePage home = new HomePage(controller, utente);
            home.setVisible(true);
            dispose();
        });

        add(btnLogin);
        setLocationRelativeTo(null);
    }
}
