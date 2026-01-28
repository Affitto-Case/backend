package com.giuseppe_tesse.turista.dao;

import java.util.List;
import java.util.Optional; // Optional per i metodi di ricerca che potrebbero non trovare nulla

import com.giuseppe_tesse.turista.model.User;

public interface UserDAO {

// ==================== CREATE ====================

    User create(User user);

// ==================== READ ====================

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);
    
// ==================== UPDATE ====================

    Optional<User> update(User user);

// ==================== DELETE ====================

    int deleteAll();

    boolean deleteById(Long id);

    boolean deleteByEmail(String email);
    
}