package com.giuseppe_tesse.turista.service;

import com.giuseppe_tesse.turista.dao.UserDAO;
import com.giuseppe_tesse.turista.exception.DuplicateUserException;
import com.giuseppe_tesse.turista.exception.UserNotFoundException;
import com.giuseppe_tesse.turista.model.User;

import java.time.LocalDate;
import java.util.List;

import com.giuseppe_tesse.turista.dto.UserMostDayBooking;
import com.giuseppe_tesse.turista.util.PasswordHasher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // ==================== CREATE ====================

    public User createUser(User user) {
        log.info("Attempt to create user - First name: {}, Last name: {}, Email: {}",
                user.getFirstName(), user.getLastName(), user.getEmail());

        if (userDAO.findByEmail(user.getEmail()).isPresent()) {
            log.warn("User creation failed - Email already exists: {}", user.getEmail());
            throw new DuplicateUserException("email", user.getEmail());
        }

        User newUser = new User(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                hashPassword(user.getPassword()),
                user.getAddress(),
                LocalDate.now());

        return userDAO.create(newUser);
    }
    // ==================== READ ====================

    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return userDAO.findAll();
    }

    public Integer getUserCount() {
        return userDAO.getUserCount();
    }

    public User getUserById(Long id) {
        log.info("Searching user by ID: {}", id);
        return userDAO.findById(id).orElseThrow(() -> {
            log.warn("User not found with ID: {}", id);
            return new UserNotFoundException(id);
        });
    }

    public User getUserByEmail(String email) {
        log.info("Searching user by email: {}", email);
        return userDAO.findByEmail(email).orElseThrow(() -> {
            log.warn("User not found with email: {}", email);
            return new UserNotFoundException(email);
        });
    }

    public List<UserMostDayBooking> getUserMostDayBooking() {
        log.info("Searching 5 user with most day booking ");
        return userDAO.findUserMostDayBooking();
    }

    // ==================== UPDATE ====================

    public User updateUser(User user) {
        log.info("Updating user with ID: {}", user.getId());

        if (userDAO.findById(user.getId()).isEmpty()) {
            log.warn("User update failed - User not found with ID: {}", user.getId());
            throw new UserNotFoundException(user.getId());
        }

        return userDAO.update(user)
                .orElseThrow(() -> new UserNotFoundException(user.getId()));
    }

    public String hashPassword(String password) {
        log.info("Updating password ");
        return PasswordHasher.hash(password);
    }

    // ==================== DELETE ====================

    public int deleteAllUsers() {
        log.info("Deleting all users");
        return userDAO.deleteAll();
    }

    public boolean deleteUserById(Long id) {
        log.info("Deleting user by ID: {}", id);

        if (userDAO.findById(id).isEmpty()) {
            log.warn("User deletion failed - User not found with ID: {}", id);
            throw new UserNotFoundException(id);
        }

        return userDAO.deleteById(id);
    }

    public boolean deleteUserByEmail(String email) {
        log.info("Deleting user by email: {}", email);

        if (userDAO.findByEmail(email).isEmpty()) {
            log.warn("User deletion failed - User not found with email: {}", email);
            throw new UserNotFoundException(email);
        }

        return userDAO.deleteByEmail(email);
    }
}
