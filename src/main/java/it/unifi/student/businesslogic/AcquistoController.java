package it.unifi.student.businesslogic;

import it.unifi.student.domain.*;
import it.unifi.student.data.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller per la gestione del processo di acquisto.
 * Implementa l'interfaccia Subject per notificare i servizi post-acquisto 
 * e la GUI in modo disaccoppiato
 */
public class AcquistoController implements Subject {

    private List<Prodotto> carrelloAttuale;
    private ProdottoDAO prodottoDAO;
    private OrdineDAO ordineDAO;
    
    // Lista degli osservatori registrati
    private List<Observer> observers = new ArrayList<>();

    /**
     * Costruttore con Dependency Injection.
     * @param prodottoDAO DAO per l'accesso al catalogo prodotti.
     * @param ordineDAO DAO per la persistenza degli ordini.
     */
    public AcquistoController(ProdottoDAO prodottoDAO, OrdineDAO ordineDAO) {
        this.carrelloAttuale = new ArrayList<>();
        this.prodottoDAO = prodottoDAO;
        this.ordineDAO = ordineDAO;
    }

    //Implementazione dell'interfaccia Subject

    @Override
    public void attach(Observer observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(TipoEvento evento, Object data) {
        for (Observer obs : observers) {
            obs.update(evento, data);
        }
    }

    // --- Metodi di Business Logic ---

    public void aggiungiPerId(String id) {
        Prodotto p = prodottoDAO.getProdottoById(id);
        if (p != null) carrelloAttuale.add(p);
    }

    public void aggiungiAlCarrello(Prodotto p) {
        if (p != null) carrelloAttuale.add(p);
    }

    //Finalizza l'acquisto creando un ordine e notificando gli osservatori.
     
    public Ordine finalizzaAcquisto(Utente utente) {
        if (carrelloAttuale.isEmpty()) return null;

        Ordine nuovoOrdine = new Ordine();
        nuovoOrdine.setCliente(utente);
        nuovoOrdine.setProdotti(new ArrayList<>(carrelloAttuale));
        nuovoOrdine.setStato("PAGATO");
        
        double totale = carrelloAttuale.stream().mapToDouble(Prodotto::getPrezzo).sum();
        nuovoOrdine.setTotale(totale);

        // Salvataggio nel database simulato
        ordineDAO.save(nuovoOrdine);
        
        // Reset del carrello
        carrelloAttuale.clear();
        
        // NOTIFICA EVENTO: Passaggio chiave per l'estensibilità del sistema [cite: 121]
        notifyObservers(TipoEvento.ACQUISTO_COMPLETATO, nuovoOrdine);
        
        return nuovoOrdine;
    }

    /**
     * Cancella un ordine esistente e notifica gli osservatori per aggiornare log e GUI.
     */
    public void cancellaOrdine(int id) {
        // Recupero l'ordine per poterlo passare nella notifica prima di rimuoverlo
        Ordine daCancellare = ordineDAO.findAll().stream()
                .filter(o -> o.getId() == id)
                .findFirst()
                .orElse(null);

        if (daCancellare != null) {
            ordineDAO.removeById(id);
            // NOTIFICA EVENTO: Permette alla View e al LogService di reagire [cite: 121]
            notifyObservers(TipoEvento.ORDINE_CANCELLATO, daCancellare);
        }
    }

    // --- Metodi Helper ---

    public List<Prodotto> getCarrello() {
        return new ArrayList<>(carrelloAttuale);
    }
    
    public List<Prodotto> getCatalogoProdotti() {
        return prodottoDAO.getAllProdotti();
    }

    public double getTotaleCarrello() {
        return carrelloAttuale.stream()
                .mapToDouble(Prodotto::getPrezzo)
                .sum();
    }

    public void rimuoviDalCarrello(Prodotto p) {
        carrelloAttuale.remove(p);
    }

    public List<Ordine> getCronologiaUtente(Utente u) {
        return ordineDAO.findAll().stream()
            .filter(o -> o.getCliente().getEmail().equals(u.getEmail()))
            .toList();
    }
}