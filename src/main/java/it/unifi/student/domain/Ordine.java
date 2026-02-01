package it.unifi.student.domain;

import java.util.ArrayList;
import java.util.List;

public class Ordine {
    private int id;
    private Utente cliente;
    private List<Prodotto> prodotti;
    private String stato;
    private double totale;

    public Ordine() {
        this.prodotti = new ArrayList<>();
    }

    // Metodi di accesso (Getter e Setter) richiesti dal Controller
    public Utente getCliente() { return cliente; }
    public void setCliente(Utente cliente) { this.cliente = cliente; }

    public List<Prodotto> getProdotti() { return prodotti; }
    public void setProdotti(List<Prodotto> prodotti) { this.prodotti = prodotti; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public double getTotale() { return totale; }
    public void setTotale(double totale) { this.totale = totale; }
}