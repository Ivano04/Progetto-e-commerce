package it.unifi.student.businesslogic;

import it.unifi.student.domain.Ordine;

public class LogService implements Observer {
    @Override
    public void update(Ordine ordine) {
        System.out.println(">>> LOG-SERVICE: Ordine di " + 
                           ordine.getCliente().getNome() + " registrato nei log.");
    }
}