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
        ordiniSalvati.add(o);
        System.out.println("LOG: Ordine salvato correttamente nel database.");
    }

    @Override
    public List<Ordine> findAll() {
        return new ArrayList<>(ordiniSalvati);
    }
}