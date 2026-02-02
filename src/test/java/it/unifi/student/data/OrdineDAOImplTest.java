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

        assertEquals(sizeBefore + 1, ordini.size(),
                "Il numero di ordini salvati dovrebbe aumentare di 1");
    }
}
