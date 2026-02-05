package it.unifi.student.domain;

public class Utente {
    private String email;
    private String nome;
    private String password;
    private boolean admin;

    // Costruttore vuoto spesso utile per i framework o i test rapidi
    public Utente() {}

    public Utente(String email, String nome, String password, boolean admin) {
        this.email = email;
        this.nome = nome;
        this.password = password;
        this.admin = admin;
    }

    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }
    public String getEmail() { return email; }
    public String getNome() { return nome; }
    public String getPassword(){ return password;}
}