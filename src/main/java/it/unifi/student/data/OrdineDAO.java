package it.unifi.student.data;

import it.unifi.student.domain.Ordine;
import java.util.List;

public interface OrdineDAO {
    void save(Ordine o);
    List<Ordine> findAll();
}



