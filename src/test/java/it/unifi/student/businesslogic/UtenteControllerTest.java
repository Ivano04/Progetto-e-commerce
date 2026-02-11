package it.unifi.student.businesslogic;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unifi.student.data.DatabaseManager;
import it.unifi.student.data.UtenteDAO;
import it.unifi.student.data.UtenteDAOImpl;
import it.unifi.student.domain.Utente;

public class UtenteControllerTest {

    private UtenteController controller;
    private UtenteDAO uDao;

    @BeforeEach
    void setUp() {
        // 1. Reset DB
        DatabaseManager.executeSqlScript("/sql/reset.sql");
        DatabaseManager.executeSqlScript("/sql/schema.sql");

        // 2. Init
        uDao = UtenteDAOImpl.getInstance();
        controller = new UtenteController(uDao);
    }

    @Test
    public void testAutentica_Successo() throws CredenzialiNonValideException {
        // Setup
        uDao.register("Mario", "mario@test.it", "pwd");

        // Action
        Utente u = controller.autentica("mario@test.it", "pwd");

        // Verify
        assertNotNull(u);
    }

    @Test
    public void testAutentica_CredenzialiInesistenti_LanciaException() {
        assertThrows(CredenzialiNonValideException.class, () -> {
            controller.autentica("non.esisto@test.it", "123");
        });
    }

    @Test
    public void testRegistrazione_NuovoUtente() {
        boolean esito = controller.registraUtente("Luigi", "luigi@test.it", "abc");
        assertTrue(esito);
        assertNotNull(uDao.findByEmailAndPassword("luigi@test.it", "abc"));
    }
}