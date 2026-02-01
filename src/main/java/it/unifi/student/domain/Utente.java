package it.unifi.student.domain;

public class Utente {
    private String email;
    private String nome;
    private String password;

    // Costruttore vuoto spesso utile per i framework o i test rapidi
    public Utente() {}

    public Utente(String email, String nome, String password) {
        this.email = email;
        this.nome = nome;
        this.password = password;
    }

    public String getEmail() { return email; }
    public String getNome() { return nome; }
}