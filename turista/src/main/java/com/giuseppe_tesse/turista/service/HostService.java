package com.giuseppe_tesse.turista.service;

import com.giuseppe_tesse.turista.dao.HostDAO;
import com.giuseppe_tesse.turista.dao.UserDAO;
import com.giuseppe_tesse.turista.model.Host;
import com.giuseppe_tesse.turista.model.User;
import com.giuseppe_tesse.turista.exception.DuplicateHostException;
import com.giuseppe_tesse.turista.exception.DuplicateUserException;
import com.giuseppe_tesse.turista.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.UUID;

@Slf4j
public class HostService {
    private final HostDAO hostDAO;
    private final UserDAO userDAO;

    public HostService(HostDAO hostDAO, UserDAO userDAO) {
        this.hostDAO = hostDAO;
        this.userDAO = userDAO;
    }

    public Host createHost(Long userId) {
        log.info("Promoting user {} to Host", userId);
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        
        if(hostDAO.findById(userId).isPresent()){
            log.warn("Host creation failed - Host already exists: {}", userId);
            throw new DuplicateHostException("userId", ""+userId);

        }
        
        Host host = new Host();
        host.setId(user.getId());
        host.setFirstName(user.getFirstName());
        host.setLastName(user.getLastName());
        host.setEmail(user.getEmail());
        host.setRegistrationDate(user.getRegistrationDate());
        host.setHost_code("HOST-" + UUID.randomUUID().toString().substring(0, 8));
        host.setTotal_bookings(0);

        return hostDAO.create(host);
    }

    public List<Host> getAllHosts() {
        return hostDAO.findAll();
    }

    public Host getHostById(Long id) {
        return hostDAO.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
}