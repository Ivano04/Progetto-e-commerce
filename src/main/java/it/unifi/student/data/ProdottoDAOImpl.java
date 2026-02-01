package it.unifi.student.data;

import it.unifi.student.domain.Prodotto;
import java.util.ArrayList;
import java.util.List;

public class ProdottoDAOImpl implements ProdottoDAO { 
    
    // 1. Istanza statica privata del Singleton
    private static ProdottoDAOImpl instance;
    private List<Prodotto> catalogo;

    // 2. Costruttore PRIVATO: nessuno fuori da questa classe può fare "new"
    private ProdottoDAOImpl() { 
        this.catalogo = new ArrayList<>();
        catalogo.add(new Prodotto("P01", "Laptop Pro", 1500.00));
        catalogo.add(new Prodotto("P02", "Smartphone Plus", 800.00));
        catalogo.add(new Prodotto("P03", "Cuffie Noise Cancelling", 250.00));
    }

    // 3. Metodo d'accesso globale
    public static synchronized ProdottoDAOImpl getInstance() {
        if (instance == null) {
            instance = new ProdottoDAOImpl();
        }
        return instance;
    }

    @Override
    public List<Prodotto> getAllProdotti() {
        return new ArrayList<>(catalogo);
    }

    @Override
    public Prodotto getProdottoById(String id) {
        return catalogo.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}