package it.unifi.student.businesslogic.desingpattern;

import java.util.List;

import it.unifi.student.domain.Prodotto;

public class ScontoPercentualeStrategy implements ScontoStrategy {
    private int percentualeSconto;

    public ScontoPercentualeStrategy(int percentualeSconto) {
        if (percentualeSconto < 0 || percentualeSconto > 100) {
            throw new IllegalArgumentException("La percentuale deve essere tra 0 e 100");
        }
        this.percentualeSconto = percentualeSconto;
    }

    @Override
    public double calcolaTotale(List<Prodotto> prodotti) {
        double totaleBase = prodotti.stream()
                .mapToDouble(Prodotto::getPrezzo)
                .sum();
        
        double sconto = (totaleBase * percentualeSconto) / 100;
        return totaleBase - sconto;
    }
}