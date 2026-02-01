package it.unifi.student.businesslogic;

import it.unifi.student.domain.Ordine;

public class EmailService implements Observer {
    @Override
    public void update(Ordine ordine) {
        System.out.println(">>> EMAIL-SERVICE: Invio email di conferma a " + 
                           ordine.getCliente().getEmail() + " per l'ordine da €" + ordine.getTotale());
    }
}