package it.unifi.student.businesslogic.controllerlogic;

import java.util.List;

import it.unifi.student.businesslogic.desingpattern.CredenzialiNonValideException;
import it.unifi.student.data.UtenteDAO;
import it.unifi.student.domain.Utente;

/**
 * Controller dedicato alla gestione degli Utenti.
 * Si occupa di autenticazione, registrazione e amministrazione account.
 */
public class UtenteController {

    private UtenteDAO utenteDAO;

    public UtenteController(UtenteDAO utenteDAO) {
        this.utenteDAO = utenteDAO;
    }

    // --- AUTENTICAZIONE ---

    public Utente autentica(String email, String password) throws CredenzialiNonValideException {
        Utente u = utenteDAO.findByEmailAndPassword(email, password);
        if (u == null) {
            throw new CredenzialiNonValideException("Credenziali non esistenti: l'utente non è registrato.");
        }
        return u;
    }

    // --- REGISTRAZIONE ---

    public boolean registraUtente(String nome, String email, String password) {
        // Validazione input basilare
        if (nome == null || nome.trim().isEmpty() || 
            email == null || email.trim().isEmpty() || 
            password == null || password.isEmpty()) {
            return false;
        }
        
        return utenteDAO.register(nome, email, password);
    }

    // --- FUNZIONALITÀ ADMIN (Gestione Utenti) ---

    public List<Utente> getListaUtenti() {
        return utenteDAO.findAll();
    }

    public void rimuoviUtente(String emailDaCancellare) {
        utenteDAO.deleteUtente(emailDaCancellare);
    }
}