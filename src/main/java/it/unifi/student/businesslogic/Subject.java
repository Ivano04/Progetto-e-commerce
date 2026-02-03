package it.unifi.student.businesslogic;

public interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    // Metodo aggiornato per passare i dettagli della notifica
    void notifyObservers(TipoEvento evento, Object data);
}