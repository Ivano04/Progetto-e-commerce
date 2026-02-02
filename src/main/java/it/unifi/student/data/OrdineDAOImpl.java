package it.unifi.student.data;

import it.unifi.student.domain.Ordine;
import java.util.ArrayList;
import java.util.List;

public class OrdineDAOImpl implements OrdineDAO {
    private static OrdineDAOImpl instance;
    private List<Ordine> ordiniSalvati;

    private OrdineDAOImpl() {
        this.ordiniSalvati = new ArrayList<>();
    }

    public static synchronized OrdineDAOImpl getInstance() {
        if (instance == null) {
            instance = new OrdineDAOImpl();
        }
        return instance;
    }

    @Override
    public void save(Ordine o) {
        // Generazione ID automatica per simulazione
        if (o.getId() <= 0) {
            o.setId(ordiniSalvati.size() + 1);
        }
        ordiniSalvati.add(o);
        System.out.println("LOG: Ordine #" + o.getId() + " salvato.");
    }

    @Override
    public List<Ordine> findAll() {
        return new ArrayList<>(ordiniSalvati);
    }
    @Override
    public void removeById(int id) {
        ordiniSalvati.removeIf(o -> o.getId() == id);
        System.out.println("LOG-DATA: Ordine " + id + " rimosso dal database.");
    }
}