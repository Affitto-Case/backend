package com.giuseppe_tesse.turista.service;

import com.giuseppe_tesse.turista.dao.BookingDAO;
import com.giuseppe_tesse.turista.dao.HostDAO;
import com.giuseppe_tesse.turista.dao.UserDAO;
import com.giuseppe_tesse.turista.dto.TopHostDTO;
import com.giuseppe_tesse.turista.model.Host;
import com.giuseppe_tesse.turista.model.User;
import com.giuseppe_tesse.turista.exception.DuplicateHostException;
import com.giuseppe_tesse.turista.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class HostService {
    private final HostDAO hostDAO;
    private final UserDAO userDAO;
    private final BookingDAO bookingDAO;

    public HostService(HostDAO hostDAO, UserDAO userDAO,BookingDAO bookingDAO) {
        this.hostDAO = hostDAO;
        this.userDAO = userDAO;
        this.bookingDAO = bookingDAO;
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
        host.setPassword(user.getPassword());
        host.setRegistrationDate(user.getRegistrationDate());
        host.setHost_code("HOST-" + UUID.randomUUID().toString().substring(0, 8));
        host.setTotal_bookings(0);

        return hostDAO.create(host);
    }

    public List<Host> getAllHosts() {
        List<Host> hosts_tmp = hostDAO.findAll();
        List<Host> hosts = new ArrayList<>();
        for (Host host : hosts_tmp) {
            int total = bookingDAO.countTotalBookingsByHostCode(host.getHost_code());
            host.setTotal_bookings(total); 
            hosts.add(host);
        }
        return hosts;
    }

    public Host getHostById(Long id) {
        Host host = hostDAO.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        int total = bookingDAO.countTotalBookingsByHostCode(host.getHost_code());
        host.setTotal_bookings(total);
        return host;
    }

    public Host getByHostCode(String host_code){
        Host host = hostDAO.findByHostCode(host_code).orElseThrow(() -> new UserNotFoundException(host_code));
        int total = bookingDAO.countTotalBookingsByHostCode(host_code);
        host.setTotal_bookings(total);
        return host;
    }

    public List<Host> getAllSuperHosts() {
        List<Host> hosts_tmp = hostDAO.getAllSuperHost();
        List<Host> hosts = new ArrayList<>();
        for (Host host : hosts_tmp) {
            int total = bookingDAO.countTotalBookingsByHostCode(host.getHost_code());
            host.setTotal_bookings(total); 
            hosts.add(host);
        }
        return hosts;
    }

public List<TopHostDTO> getTopHostsLastMonth() {
        List<TopHostDTO> hosts = hostDAO.getTopHostsLastMonth();

        return hosts;
    }

public void updateHostStats(Host host) {
    log.info("Updating stats for Host Code: {}. Bookings: {}, SuperHost: {}", 
              host.getHost_code(), host.getTotal_bookings(), host.isSuperHost());
    hostDAO.updateHostStats(host.getId(), host.getTotal_bookings(), host.isSuperHost());
}
    
}