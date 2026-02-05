package it.unifi.student.data;

import java.util.List;

import it.unifi.student.domain.Utente;

public interface UtenteDAO {
    Utente findByEmailAndPassword(String email, String password);
    List<Utente> findAll();
    void deleteUtente(String email);

    // --- MODIFICA QUESTA RIGA ---
    boolean register(String nome, String email, String password);
}
