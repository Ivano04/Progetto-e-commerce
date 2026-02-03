package it.unifi.student.businesslogic;
public interface Observer {
    // Ora l'observer sa COSA è successo e riceve i DATI correlati
    void update(TipoEvento evento, Object data);
}