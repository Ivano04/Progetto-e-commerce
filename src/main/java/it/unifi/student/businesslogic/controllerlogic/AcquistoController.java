package it.unifi.student.businesslogic.controllerlogic;

import java.util.ArrayList;
import java.util.List;

import it.unifi.student.businesslogic.desingpattern.NessunoScontoStrategy;
import it.unifi.student.businesslogic.desingpattern.Observer;
import it.unifi.student.businesslogic.desingpattern.ScontoPercentualeStrategy;
import it.unifi.student.businesslogic.desingpattern.ScontoStrategy;
import it.unifi.student.businesslogic.desingpattern.Subject;
import it.unifi.student.businesslogic.desingpattern.TipoEvento;
import it.unifi.student.data.CouponDAO;
import it.unifi.student.data.OrdineDAO;
import it.unifi.student.domain.Coupon;
import it.unifi.student.domain.Ordine;
import it.unifi.student.domain.Prodotto;
import it.unifi.student.domain.Utente;

/**
 * Controller dedicato SOLO al processo di acquisto.
 * Gestisce il Carrello, l'applicazione degli sconti e la finalizzazione dell'ordine.
 */
public class AcquistoController implements Subject {

    private List<Prodotto> carrelloAttuale;
    private OrdineDAO ordineDAO;
    private CouponDAO couponDAO; // Serve SOLO per leggere se un coupon esiste e applicarlo
    
    // Lista degli osservatori registrati
    private List<Observer> observers = new ArrayList<>();

    // Campo per la Strategy
    private ScontoStrategy scontoStrategy;

    // Costruttore alleggerito: via UtenteDAO e ProdottoDAO
    public AcquistoController(OrdineDAO ordineDAO, CouponDAO couponDAO) {
        this.carrelloAttuale = new ArrayList<>();
        this.ordineDAO = ordineDAO;
        this.couponDAO = couponDAO;
        this.scontoStrategy = new NessunoScontoStrategy();
    }

    // --- IMPLEMENTAZIONE SUBJECT ---

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

    // --- LOGICA CARRELLO ---

    public void aggiungiAlCarrello(Prodotto p) {
        if (p != null) carrelloAttuale.add(p);
    }

    public void rimuoviDalCarrello(Prodotto p) {
        carrelloAttuale.remove(p);
    }
    
    public void svuotaCarrello() {
        carrelloAttuale.clear();
        this.scontoStrategy = new NessunoScontoStrategy(); // Reset strategia
    }

    public List<Prodotto> getCarrello() {
        return new ArrayList<>(carrelloAttuale);
    }

    // --- STRATEGY & SCONTI ---

    public void setScontoStrategy(ScontoStrategy scontoStrategy) {
        if (scontoStrategy == null) {
            throw new IllegalArgumentException("La strategia non può essere null");
        }
        this.scontoStrategy = scontoStrategy;
    }

    public double getTotaleCarrello() {
        return scontoStrategy.calcolaTotale(carrelloAttuale);
    }

    // Metodo per il Cliente: Verifica e applica il coupon
    public boolean applicaCoupon(String codiceInput) {
        Coupon c = couponDAO.findByCodice(codiceInput.toUpperCase());
        if (c != null) {
            // Se esiste, cambio la strategia a runtime
            this.setScontoStrategy(new ScontoPercentualeStrategy(c.getPercentualeSconto()));
            return true;
        }
        return false;
    }

    // --- FINALIZZAZIONE ORDINE ---

    public Ordine finalizzaAcquisto(Utente utente) {
        if (carrelloAttuale.isEmpty()) return null;

        Ordine nuovoOrdine = new Ordine();
        nuovoOrdine.setCliente(utente);
        nuovoOrdine.setProdotti(new ArrayList<>(carrelloAttuale));
        nuovoOrdine.setStato("PAGATO");
        
        // Calcolo totale con la strategia corrente
        nuovoOrdine.setTotale(getTotaleCarrello());

        // Salvataggio nel database
        ordineDAO.save(nuovoOrdine);
        
        // Notifica e Reset
        notifyObservers(TipoEvento.ACQUISTO_COMPLETATO, nuovoOrdine);
        svuotaCarrello();
        
        return nuovoOrdine;
    }
     
    public void cancellaOrdine(int id) {
        Ordine daCancellare = ordineDAO.findAll().stream()
                .filter(o -> o.getId() == id)
                .findFirst()
                .orElse(null);

        if (daCancellare != null) {
            ordineDAO.removeById(id);
            notifyObservers(TipoEvento.ORDINE_CANCELLATO, daCancellare);
        }
    }

    // --- HELPER ---

    public List<Ordine> getCronologiaUtente(Utente u) {
        return ordineDAO.findAll().stream()
            .filter(o -> o.getCliente().getEmail().equals(u.getEmail()))
            .toList();
    }
}