package it.unifi.student.data;

import it.unifi.student.domain.Prodotto;
import java.util.ArrayList;
import java.util.List;

public class ProdottoDAOImpl implements ProdottoDAO { 
    
    // 1. L'istanza deve essere della classe stessa, NON del Test
    private static ProdottoDAOImpl instance; 
    private List<Prodotto> catalogo;

    // 2. Costruttore PRIVATO
    private ProdottoDAOImpl() { 
        this.catalogo = new ArrayList<>();
        catalogo.add(new Prodotto("P01", "Laptop Pro", 1500.00));
        catalogo.add(new Prodotto("P02", "Smartphone Plus", 800.00));
        catalogo.add(new Prodotto("P03", "Cuffie Noise Cancelling", 250.00));
        catalogo.add(new Prodotto("P04", "Monitor 4K", 350.00));
        catalogo.add(new Prodotto("P05", "Tastiera Meccanica", 120.00));
        catalogo.add(new Prodotto("P06", "Mouse Wireless", 80.00));
        catalogo.add(new Prodotto("P07", "Webcam HD", 95.00));
        catalogo.add(new Prodotto("P08", "Sedia Gaming", 299.00));
    }

    // 3. Metodo d'accesso globale: deve restituire l'istanza corretta
    public static synchronized ProdottoDAOImpl getInstance() {
        if (instance == null) {
            instance = new ProdottoDAOImpl(); // Crea l'oggetto reale
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