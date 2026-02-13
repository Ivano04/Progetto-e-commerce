package it.unifi.student.businesslogic.desingpattern;

import java.util.List;

import it.unifi.student.domain.Prodotto;

/**
 * Interfaccia Strategy per il calcolo del totale dell'ordine.
 * Permette di definire diversi algoritmi di sconto intercambiabili.
 */
public interface ScontoStrategy {
    double calcolaTotale(List<Prodotto> prodotti);
}

