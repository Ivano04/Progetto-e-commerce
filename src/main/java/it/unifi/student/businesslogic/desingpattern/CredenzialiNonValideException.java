package it.unifi.student.businesslogic.desingpattern;

public class CredenzialiNonValideException extends Exception {
    public CredenzialiNonValideException(String messaggio) {
        super(messaggio);
    }
}
