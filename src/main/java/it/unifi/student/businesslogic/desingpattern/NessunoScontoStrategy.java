package it.unifi.student.businesslogic.desingpattern;

import java.util.List;

import it.unifi.student.domain.Prodotto;

public class NessunoScontoStrategy implements ScontoStrategy {
    @Override
    public double calcolaTotale(List<Prodotto> prodotti) {
        if (prodotti == null) return 0.0;
        // Logica standard: somma dei prezzi base
        return prodotti.stream()
                .mapToDouble(Prodotto::getPrezzo)
                .sum();
    }
}