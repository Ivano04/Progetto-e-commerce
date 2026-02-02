package it.unifi.student.businesslogic;

import it.unifi.student.domain.*;
import it.unifi.student.data.*;
import java.util.ArrayList;
import java.util.List;

// Il controller ora implementa Subject per notificare i servizi post-acquisto
public class AcquistoController implements Subject {

    private List<Prodotto> carrelloAttuale;
    private ProdottoDAO prodottoDAO;
    private OrdineDAO ordineDAO;
    
    // Lista interna degli osservatori
    private List<Observer> observers = new ArrayList<>();
    private Ordine ultimoOrdineFinalizzato;

    public AcquistoController(ProdottoDAO prodottoDAO, OrdineDAO ordineDAO) {
        this.carrelloAttuale = new ArrayList<>();
        this.prodottoDAO = prodottoDAO;
        this.ordineDAO = ordineDAO;
    }

    // --- Metodi interfaccia Subject ---
    @Override
    public void attach(Observer observer) {
        if (observer != null) observers.add(observer);
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer obs : observers) {
            obs.update(ultimoOrdineFinalizzato);
        }
    }
    // -----------------------------------

    public void aggiungiPerId(String id) {
        Prodotto p = prodottoDAO.getProdottoById(id);
        if (p != null) carrelloAttuale.add(p);
    }

    public void aggiungiAlCarrello(Prodotto p) {
        if (p != null) carrelloAttuale.add(p);
    }

    public Ordine finalizzaAcquisto(Utente utente) {
        if (carrelloAttuale.isEmpty()) return null;

        Ordine nuovoOrdine = new Ordine();
        nuovoOrdine.setCliente(utente);
        nuovoOrdine.setProdotti(new ArrayList<>(carrelloAttuale));
        nuovoOrdine.setStato("IN_ELABORAZIONE");
        
        double totale = carrelloAttuale.stream().mapToDouble(Prodotto::getPrezzo).sum();
        nuovoOrdine.setTotale(totale);

        ordineDAO.save(nuovoOrdine);
        
        // Prepariamo la notifica salvando lo stato interno
        this.ultimoOrdineFinalizzato = nuovoOrdine;
        carrelloAttuale.clear();
        
        // NOTIFICA: Questo è il passaggio chiave del pattern [cite: 121]
        notifyObservers();
        
        return nuovoOrdine;
    }

    public List<Prodotto> getCarrello() {
        return carrelloAttuale;
    }
    
    public List<Prodotto> getCatalogoProdotti() {
    return prodottoDAO.getAllProdotti();
}
}