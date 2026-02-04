package it.unifi.student.data;

import it.unifi.student.domain.Utente;
import it.unifi.student.businesslogic.CredenzialiNonValideException;

public interface UtenteDAO {
    // Restituisce l'utente se le credenziali sono corrette, altrimenti null
    Utente findByEmailAndPassword(String email, String password);
}
