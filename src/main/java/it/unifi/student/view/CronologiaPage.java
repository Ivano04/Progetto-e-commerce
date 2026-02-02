package it.unifi.student.view;

import it.unifi.student.businesslogic.AcquistoController;
import it.unifi.student.domain.Ordine;
import it.unifi.student.domain.Prodotto;
import it.unifi.student.domain.Utente;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class CronologiaPage extends JFrame {

    private AcquistoController controller;
    private Utente utente;
    private JPanel listaPanel;

    public CronologiaPage(AcquistoController controller, Utente utente) {
        this.controller = controller;
        this.utente = utente;

        setTitle("Cronologia Acquisti - " + utente.getNome());
        setSize(450, 550);
        setLayout(new BorderLayout());

        // Pannello principale con scroll per contenere molti ordini
        listaPanel = new JPanel();
        listaPanel.setLayout(new BoxLayout(listaPanel, BoxLayout.Y_AXIS));
        
        refreshLista();

        add(new JScrollPane(listaPanel), BorderLayout.CENTER);

        JButton btnChiudi = new JButton("Chiudi");
        btnChiudi.addActionListener(e -> dispose());
        add(btnChiudi, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private void refreshLista() {
        listaPanel.removeAll();
        List<Ordine> ordini = controller.getCronologiaUtente(utente);

        if (ordini.isEmpty()) {
            listaPanel.add(new JLabel("Nessun ordine effettuato."));
        } else {
            for (Ordine o : ordini) {
                // Creiamo un contenitore per il singolo ordine
                JPanel ordineBox = new JPanel();
                ordineBox.setLayout(new BoxLayout(ordineBox, BoxLayout.Y_AXIS));
                ordineBox.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(), 
                        "Ordine #" + o.getId(), 
                        TitledBorder.LEFT, 
                        TitledBorder.TOP));

                // Elenco dei prodotti all'interno dell'ordine
                for (Prodotto p : o.getProdotti()) {
                    ordineBox.add(new JLabel("  • " + p.getNome() + " (€" + p.getPrezzo() + ")"));
                }

                // Riga per totale e pulsante rimozione
                JPanel rigaFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JLabel lblTotale = new JLabel("TOTALE: €" + o.getTotale());
                lblTotale.setFont(new Font("Arial", Font.BOLD, 12));
                
                JButton btnElimina = new JButton("Rimuovi");
                btnElimina.setForeground(Color.RED);
                btnElimina.addActionListener(e -> {
                    controller.cancellaOrdine(o.getId());
                    refreshLista(); // Ricarica la vista dopo la cancellazione
                });

                rigaFooter.add(lblTotale);
                rigaFooter.add(btnElimina);
                
                ordineBox.add(rigaFooter);

                // Aggiungiamo un po' di spazio tra gli ordini
                listaPanel.add(ordineBox);
                listaPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        listaPanel.revalidate();
        listaPanel.repaint();
    }
}