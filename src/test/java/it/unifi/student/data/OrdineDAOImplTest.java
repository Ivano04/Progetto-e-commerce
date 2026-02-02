package it.unifi.student.data;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import it.unifi.student.domain.Ordine;
import java.util.List;

public class OrdineDAOImplTest {

    @Test
    void testSaveAndFindAll() {
        OrdineDAO dao = OrdineDAOImpl.getInstance();
        int sizeBefore = dao.findAll().size();

        Ordine o = new Ordine();
        dao.save(o);

        List<Ordine> ordini = dao.findAll();
        assertEquals(sizeBefore + 1, ordini.size(), "Il numero di ordini salvati dovrebbe aumentare di 1");
    }

    @Test
    void testRimozioneOrdine_PhysicalDelete() {
        OrdineDAO dao = OrdineDAOImpl.getInstance();
        Ordine o = new Ordine();
        dao.save(o);
        int idGenerato = o.getId();

        dao.removeById(idGenerato);

        List<Ordine> tutti = dao.findAll();
        boolean ancoraPresente = tutti.stream().anyMatch(ord -> ord.getId() == idGenerato);
        assertFalse(ancoraPresente, "L'ordine rimosso è ancora presente nel DAO.");
    }
    
    @Test
    void testGenerazioneIdProgressivo() {
        OrdineDAO dao = OrdineDAOImpl.getInstance();
        Ordine o1 = new Ordine();
        Ordine o2 = new Ordine();
        
        dao.save(o1);
        dao.save(o2);

        assertTrue(o2.getId() > o1.getId(), "L'ID del secondo ordine deve essere superiore al primo.");
    }
}