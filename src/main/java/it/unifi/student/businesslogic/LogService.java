package it.unifi.student.businesslogic;

import it.unifi.student.domain.Ordine;

public class LogService implements Observer {
    @Override
    public void update(TipoEvento evento, Object data) {
        Ordine o = (Ordine) data;
        switch (evento) {
            case ACQUISTO_COMPLETATO -> System.out.println(">>> LOG: Vendita registrata - Ordine #" + o.getId());
            case ORDINE_CANCELLATO -> System.out.println(">>> LOG: Ordine ANNULLATO - Ordine #" + o.getId());
        }
    }
}