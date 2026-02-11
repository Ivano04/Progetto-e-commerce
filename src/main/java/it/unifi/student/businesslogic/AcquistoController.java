package it.unifi.student.businesslogic;

import java.util.ArrayList;
import java.util.List;

import it.unifi.student.data.OrdineDAO;
import it.unifi.student.data.ProdottoDAO;
import it.unifi.student.data.UtenteDAO;
import it.unifi.student.domain.Ordine;
import it.unifi.student.domain.Prodotto;
import it.unifi.student.domain.Utente;
import it.unifi.student.data.CouponDAO;
import it.unifi.student.domain.Coupon;

/**
 * Controller per la gestione del processo di acquisto.
 * Implementa l'interfaccia Subject per notificare i servizi post-acquisto 
 * e la GUI in modo disaccoppiato
 */
public class AcquistoController implements Subject {

    private List<Prodotto> carrelloAttuale;
    private ProdottoDAO prodottoDAO;
    private OrdineDAO ordineDAO;
    private UtenteDAO utenteDAO;
    
    // Lista degli osservatori registrati
    private List<Observer> observers = new ArrayList<>();

    //Campo per la Strategy
    private ScontoStrategy scontoStrategy;
    private CouponDAO couponDAO;

    /**
     * Costruttore con Dependency Injection.
     *  prodottoDAO DAO per l'accesso al catalogo prodotti.
     *  ordineDAO DAO per la persistenza degli ordini.
     *  utenteDAO DAO per la gestione utenti.
     */
    public AcquistoController(ProdottoDAO prodottoDAO, OrdineDAO ordineDAO, UtenteDAO utenteDAO, CouponDAO couponDAO) {
        this.carrelloAttuale = new ArrayList<>();
        this.prodottoDAO = prodottoDAO;
        this.ordineDAO = ordineDAO;
        this.utenteDAO = utenteDAO;
        this.couponDAO = couponDAO;
        this.scontoStrategy = new NessunoScontoStrategy();
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

    //Metodi della Business Logic 

    public void aggiungiPerId(String id) {
        Prodotto p = prodottoDAO.getProdottoById(id);
        if (p != null) carrelloAttuale.add(p);
    }

    public void aggiungiAlCarrello(Prodotto p) {
        if (p != null) carrelloAttuale.add(p);
    }

    public void setScontoStrategy(ScontoStrategy scontoStrategy) {
        if (scontoStrategy == null) {
            throw new IllegalArgumentException("La strategia non può essere null");
        }
        this.scontoStrategy = scontoStrategy;
    }

    // Finalizza l'acquisto creando un ordine e notificando gli osservatori.
    public Ordine finalizzaAcquisto(Utente utente) {
        if (carrelloAttuale.isEmpty()) return null;

        Ordine nuovoOrdine = new Ordine();
        nuovoOrdine.setCliente(utente);
        nuovoOrdine.setProdotti(new ArrayList<>(carrelloAttuale));
        nuovoOrdine.setStato("PAGATO");
        
        double totaleCalcolato = scontoStrategy.calcolaTotale(carrelloAttuale);
        nuovoOrdine.setTotale(totaleCalcolato);

        // Salvataggio nel database
        ordineDAO.save(nuovoOrdine);
        
        // Reset del carrello
        carrelloAttuale.clear();
        
        this.scontoStrategy = new NessunoScontoStrategy();
        // NOTIFICA EVENTO: Passaggio chiave per l'estensibilità del sistema
        notifyObservers(TipoEvento.ACQUISTO_COMPLETATO, nuovoOrdine);
        
        return nuovoOrdine;
    }

    
    // Cancella un ordine esistente e notifica gli osservatori per aggiornare log e GUI.
     
    public void cancellaOrdine(int id) {
        // Recupero l'ordine per poterlo passare nella notifica prima di rimuoverlo
        Ordine daCancellare = ordineDAO.findAll().stream()
                .filter(o -> o.getId() == id)
                .findFirst()
                .orElse(null);

        if (daCancellare != null) {
            ordineDAO.removeById(id);
            // NOTIFICA EVENTO: Permette alla View e al LogService di reagire
            notifyObservers(TipoEvento.ORDINE_CANCELLATO, daCancellare);
        }
    }

    // Metodi Helper

    public List<Prodotto> getCarrello() {
        return new ArrayList<>(carrelloAttuale);
    }
    
    public List<Prodotto> getCatalogoProdotti() {
        return prodottoDAO.getAllProdotti();
    }

    public double getTotaleCarrello() {
        return scontoStrategy.calcolaTotale(carrelloAttuale);
    }

    public void rimuoviDalCarrello(Prodotto p) {
        carrelloAttuale.remove(p);
    }

    public List<Ordine> getCronologiaUtente(Utente u) {
        return ordineDAO.findAll().stream()
            .filter(o -> o.getCliente().getEmail().equals(u.getEmail()))
            .toList();
    }

    //GESTIONE UTENTI (Login e Registrazione)

    public Utente autentica(String email, String password) throws CredenzialiNonValideException {
        Utente u = utenteDAO.findByEmailAndPassword(email, password);
        if (u == null) {
            throw new CredenzialiNonValideException("Credenziali non esistenti: l'utente non è registrato.");
        }
        return u;
    }

    
    // Metodo aggiornato per gestire 3 campi: Nome, Email, Password.
     
    public boolean registraUtente(String nome, String email, String password) {
        // Controllo che nessuno dei tre campi sia vuoto
        if (nome == null || nome.isEmpty() || 
            email == null || email.isEmpty() || 
            password == null || password.isEmpty()) {
            return false;
        }
        
        // Passa tutti e tre i dati al DAO
        return utenteDAO.register(nome, email, password);
    }

    // Restituisce la lista di tutti gli utenti (solo per l'admin)
    public List<Utente> getListaUtenti() {
        return utenteDAO.findAll();
    }

    // Cancella un utente, ma prima controlla i permessi
    public void rimuoviUtente(String emailDaCancellare) {
        // Qui potremmo aggiungere un controllo se l'utente corrente è admin, 
        // ma per ora lo gestiremo nascondendo il bottone nella grafica.
        utenteDAO.deleteUtente(emailDaCancellare);
    }

    // NUOVI METODI AGGIUNTI PER LA GESTIONE PRODOTTI ADMIN
    
    public void aggiungiNuovoProdotto(String id, String nome, double prezzo) {
        if (esisteProdotto(id)) {
            throw new IllegalArgumentException("Errore: L'ID " + id + " è già presente nel catalogo.");
        }
        Prodotto p = new Prodotto(id, nome, prezzo);
        prodottoDAO.save(p);
    }

    public void rimuoviProdotto(String id) {
        prodottoDAO.delete(id);
    }

    public boolean esisteProdotto(String id) {
        return prodottoDAO.getProdottoById(id) != null;
    }
    public void creaNuovoCoupon(String codice, int percentuale) {
        if (percentuale <= 0 || percentuale > 100) {
            throw new IllegalArgumentException("La percentuale deve essere tra 1 e 100.");
        }
        if (codice == null || codice.trim().isEmpty()) {
             throw new IllegalArgumentException("Il codice non può essere vuoto.");
        }
        // Verifica se esiste già
        if (couponDAO.findByCodice(codice.toUpperCase()) != null) {
            throw new IllegalArgumentException("Questo codice coupon esiste già!");
        }

        Coupon c = new Coupon(codice.toUpperCase(), percentuale);
        couponDAO.save(c);
    }

    // Metodo per il Cliente: Verifica e applica il coupon
    public boolean applicaCoupon(String codiceInput) {
        Coupon c = couponDAO.findByCodice(codiceInput.toUpperCase());
        if (c != null) {
            // Se esiste, cambio la strategia a runtime usando la percentuale del DB
            this.setScontoStrategy(new ScontoPercentualeStrategy(c.getPercentualeSconto()));
            return true;
        }
        return false;
    }

    public List<String> getListaCodiciCoupon() {
        return couponDAO.getAllCodici();
    }

    public void rimuoviCoupon(String codice) {
        if (codice != null && !codice.isEmpty()) {
            couponDAO.delete(codice);
            // Per semplicità, resettiamo la strategia se corrisponde
        }
    }

}