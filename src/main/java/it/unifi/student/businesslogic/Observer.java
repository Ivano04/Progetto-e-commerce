package it.unifi.student.businesslogic;

import it.unifi.student.domain.Ordine;

public interface Observer {
    // Metodo chiamato quando il Subject notifica un cambiamento
    void update(Ordine ordine);
}