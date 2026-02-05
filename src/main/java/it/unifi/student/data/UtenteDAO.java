package it.unifi.student.data;

import it.unifi.student.domain.Utente;

public interface UtenteDAO {
    Utente findByEmailAndPassword(String email, String password);
    
    // --- MODIFICA QUESTA RIGA ---
    boolean register(String nome, String email, String password);
}
