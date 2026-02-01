package it.unifi.student.domain;

public class Prodotto {
    private String id;
    private String nome;
    private double prezzo;

    public Prodotto(String id, String nome, double prezzo) {
        this.id = id;
        this.nome = nome;
        this.prezzo = prezzo;
    }

    // Getter necessari per la Business Logic e i Test
    public String getId() { return id; }
    public String getNome() { return nome; }
    public double getPrezzo() { return prezzo; }
}