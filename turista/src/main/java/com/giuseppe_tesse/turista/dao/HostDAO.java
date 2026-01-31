package com.giuseppe_tesse.turista.dao;

import com.giuseppe_tesse.turista.dto.TopHostDTO;
import com.giuseppe_tesse.turista.model.Host;
import java.util.List;
import java.util.Optional;

public interface HostDAO {
    Host create(Host host);
    List<Host> findAll();
    Optional<Host> findById(Long id);
    Optional<Host> findByHostCode(String hostCode);
    List<Host> getAllSuperHost();
    List<TopHostDTO> getTopHostsLastMonth();
    Optional<Host> update(Host host);
    void updateSuperHostStatus(Long hostId, boolean isSuper);
    boolean deleteById(Long id);
}