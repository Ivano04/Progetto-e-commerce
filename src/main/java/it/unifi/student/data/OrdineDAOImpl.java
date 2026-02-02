package it.unifi.student.data;

import it.unifi.student.domain.Ordine;
import java.util.ArrayList;
import java.util.List;

public class OrdineDAOImpl implements OrdineDAO {
    private static OrdineDAOImpl instance;
    private List<Ordine> ordiniSalvati = new ArrayList<>();
    
    private int nextId = 1; 

    private OrdineDAOImpl() {}

    public static synchronized OrdineDAOImpl getInstance() {
        if (instance == null) instance = new OrdineDAOImpl();
        return instance;
    }

    @Override
    public void save(Ordine o) {
        // Assegniamo l'ID solo se l'ordine non ne ha già uno
        if (o.getId() <= 0) {
            o.setId(nextId++); 
        }
        ordiniSalvati.add(o);
        System.out.println("LOG: Ordine #" + o.getId() + " salvato.");
    }

    @Override
    public void removeById(int id) {
        ordiniSalvati.removeIf(o -> o.getId() == id);
    }

    @Override
    public List<Ordine> findAll() {
        return new ArrayList<>(ordiniSalvati);
    }

    //  resetta i test in modo da renderli indipendenti
    public void clear() {
        ordiniSalvati.clear();
        nextId = 1; // Riporta il contatore a 1 per il prossimo test
    }
}