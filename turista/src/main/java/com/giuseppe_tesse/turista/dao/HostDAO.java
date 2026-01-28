package com.giuseppe_tesse.turista.dao;

import com.giuseppe_tesse.turista.model.Host;
import java.util.List;
import java.util.Optional;

public interface HostDAO {
    Host create(Host host);
    List<Host> findAll();
    Optional<Host> findById(Long id);
    Optional<Host> findByHostCode(String hostCode);
    Optional<Host> update(Host host);
    boolean deleteById(Long id);
}