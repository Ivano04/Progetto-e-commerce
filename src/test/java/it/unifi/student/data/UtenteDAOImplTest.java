package it.unifi.student.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import it.unifi.student.domain.Utente;

public class UtenteDAOImplTest {

    @BeforeEach
    void setUp() {
        // Re-inizializzazione del database prima di ogni test 
        // Questo garantisce che l'utente 'Rossi' sia effettivamente presente
        DatabaseManager.executeSqlScript("/sql/reset.sql");
        DatabaseManager.executeSqlScript("/sql/schema.sql");
        DatabaseManager.executeSqlScript("/sql/default.sql");
    }

    @Test
    void testFindByEmailAndPassword_Successo() {
        UtenteDAO dao = UtenteDAOImpl.getInstance();
        // 'Rossi' è l'utente inserito via default.sql nel progetto
        Utente trovato = dao.findByEmailAndPassword("Rossi", "Rossi");

        assertNotNull(trovato, "L'utente Rossi dovrebbe essere presente");
        assertEquals("Mario", trovato.getNome());
    }

    @Test
    void testFindByEmailAndPassword_Fallimento() {
        UtenteDAO dao = UtenteDAOImpl.getInstance();
        Utente trovato = dao.findByEmailAndPassword("email@errata.it", "pwd");

        assertNull(trovato, "Per credenziali errate il DAO deve restituire null");
    }
}