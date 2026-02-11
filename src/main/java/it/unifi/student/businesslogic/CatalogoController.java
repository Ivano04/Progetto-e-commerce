package it.unifi.student.businesslogic;

import java.util.List;
import it.unifi.student.data.ProdottoDAO;
import it.unifi.student.data.CouponDAO;
import it.unifi.student.domain.Prodotto;
import it.unifi.student.domain.Coupon;

/**
 * Controller per la gestione del Catalogo (Prodotti e Coupon).
 * Usato principalmente dall'Amministratore o dalla HomePage per visualizzare la merce.
 */
public class CatalogoController {

    private ProdottoDAO prodottoDAO;
    private CouponDAO couponDAO;

    public CatalogoController(ProdottoDAO prodottoDAO, CouponDAO couponDAO) {
        this.prodottoDAO = prodottoDAO;
        this.couponDAO = couponDAO;
    }

    // --- GESTIONE PRODOTTI ---

    public List<Prodotto> getCatalogoProdotti() {
        return prodottoDAO.getAllProdotti();
    }

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

    // --- GESTIONE COUPON (Lato Admin) ---

    public void creaNuovoCoupon(String codice, int percentuale) {
        if (percentuale <= 0 || percentuale > 100) {
            throw new IllegalArgumentException("La percentuale deve essere tra 1 e 100.");
        }
        if (codice == null || codice.trim().isEmpty()) {
             throw new IllegalArgumentException("Il codice non può essere vuoto.");
        }
        if (couponDAO.findByCodice(codice.toUpperCase()) != null) {
            throw new IllegalArgumentException("Questo codice coupon esiste già!");
        }

        Coupon c = new Coupon(codice.toUpperCase(), percentuale);
        couponDAO.save(c);
    }

    public void rimuoviCoupon(String codice) {
        if (codice != null && !codice.isEmpty()) {
            couponDAO.delete(codice);
        }
    }

    public List<String> getListaCodiciCoupon() {
        return couponDAO.getAllCodici();
    }
}