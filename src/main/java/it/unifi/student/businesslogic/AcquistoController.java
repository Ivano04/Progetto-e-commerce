package it.unifi.student.businesslogic;

import it.unifi.student.domain.*;
import it.unifi.student.data.*;
import java.util.ArrayList;
import java.util.List;

public class AcquistoController {

    private List<Prodotto> carrelloAttuale;
    private ProdottoDAO prodottoDAO;
    private OrdineDAO ordineDAO;

    // Dependency Injection: il controller riceve i DAO dall'esterno
    public AcquistoController(ProdottoDAO prodottoDAO, OrdineDAO ordineDAO) {
        this.carrelloAttuale = new ArrayList<>();
        this.prodottoDAO = prodottoDAO;
        this.ordineDAO = ordineDAO;
    }

    public void aggiungiPerId(String id) {
        Prodotto p = prodottoDAO.getProdottoById(id);
        if (p != null) {
            carrelloAttuale.add(p);
        }
    }

    public void aggiungiAlCarrello(Prodotto p) {
        if (p != null) {
            carrelloAttuale.add(p);
        }
    }

    public Ordine finalizzaAcquisto(Utente utente) {
        if (carrelloAttuale.isEmpty()) {
            return null;
        }

        Ordine nuovoOrdine = new Ordine();
        nuovoOrdine.setCliente(utente);
        nuovoOrdine.setProdotti(new ArrayList<>(carrelloAttuale));
        nuovoOrdine.setStato("IN_ELABORAZIONE");
        
        double totale = carrelloAttuale.stream()
                                      .mapToDouble(Prodotto::getPrezzo)
                                      .sum();
        nuovoOrdine.setTotale(totale);

        // Salvataggio tramite DAO
        ordineDAO.save(nuovoOrdine);

        carrelloAttuale.clear();
        return nuovoOrdine;
    }

    public List<Prodotto> getCarrello() {
        return carrelloAttuale;
    }
}