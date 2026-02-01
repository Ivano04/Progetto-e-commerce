package it.unifi.student.data;

import it.unifi.student.domain.Prodotto;
import java.util.List;

public interface ProdottoDAO {
    // Recupera tutti i prodotti disponibili nel database
    List<Prodotto> getAllProdotti();
    
    // Cerca un prodotto specifico per ID
    Prodotto getProdottoById(String id);
}