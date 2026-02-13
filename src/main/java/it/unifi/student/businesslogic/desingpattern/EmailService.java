package it.unifi.student.businesslogic.desingpattern;

import it.unifi.student.domain.Ordine;
public class EmailService implements Observer {
    @Override
    public void update(TipoEvento evento, Object data) {
        if (evento == TipoEvento.ACQUISTO_COMPLETATO) {
            Ordine o = (Ordine) data;
            System.out.println(">>> EMAIL-SERVICE: Conferma ordine inviata a " + o.getCliente().getEmail());
        }
        // L'EmailService ignora la cancellazione (o potrebbe inviare una mail di rimborso)
    }
}