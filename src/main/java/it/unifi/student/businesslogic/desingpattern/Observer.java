package it.unifi.student.businesslogic.desingpattern;
public interface Observer {
    // Ora l'observer sa COSA è successo e riceve i DATI correlati
    void update(TipoEvento evento, Object data);
}