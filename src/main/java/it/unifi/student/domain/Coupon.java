package it.unifi.student.domain;

public class Coupon {
    private String codice;
    private int percentualeSconto;

    public Coupon(String codice, int percentualeSconto) {
        this.codice = codice;
        this.percentualeSconto = percentualeSconto;
    }

    public String getCodice() { return codice; }
    public int getPercentualeSconto() { return percentualeSconto; }
}