package com.giuseppe_tesse.turista.controller;

import com.giuseppe_tesse.turista.model.User;
import com.giuseppe_tesse.turista.service.UserService;
import com.giuseppe_tesse.turista.exception.DuplicateUserException;
import com.giuseppe_tesse.turista.exception.UserNotFoundException;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class UserController implements Controller {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void registerRoutes(Javalin app) {
        app.post("/api/v1/users", this::createUser);
        app.get("/api/v1/users/{id}", this::getUserById);
        app.get("/api/v1/users", this::getAllUsers);
        app.get("/api/v1/users/email/{email}", this::getUserByEmail);
        app.put("/api/v1/users/{id}", this::updateUser);
        app.delete("/api/v1/users/{id}", this::deleteUserById);
        app.delete("/api/v1/users", this::deleteAllUsers);
        app.delete("/api/v1/users/email/{email}", this::deleteUserByEmail);
    }

    // ==================== CREATE ====================
    private void createUser(Context ctx) {
        log.info("POST /api/v1/users - Request to create user");
        User user = ctx.bodyAsClass(User.class); // Corretto da Utente.class

        try {
            User createdUser = userService.createUser(
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getAddress()
            );
            ctx.status(HttpStatus.CREATED).json(createdUser);
            log.info("User created successfully: {}", createdUser);
        } catch (DuplicateUserException e) {
            log.error("Error creating user: {}", e.getMessage());
            ctx.status(HttpStatus.CONFLICT).result(e.getMessage());
        }
    }

    // ==================== READ ====================
    private void getUserById(Context ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        log.info("GET /api/v1/users/{} - Request to fetch user by ID", id);
        try {
            User user = userService.getUserById(id);
            ctx.status(HttpStatus.OK).json(user);
            log.info("User retrieved successfully: {}", user);
        } catch (UserNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void getAllUsers(Context ctx) {
        log.info("GET /api/v1/users - Request to fetch all users");
        List<User> users = userService.getAllUsers();
        ctx.status(HttpStatus.OK).json(users);
        log.info("All users retrieved successfully, total: {}", users.size());
    }

    private void getUserByEmail(Context ctx) {
        String email = ctx.pathParam("email");
        log.info("GET /api/v1/users/email/{} - Request to fetch user by email", email);
        try {
            User user = userService.getUserByEmail(email);
            ctx.status(HttpStatus.OK).json(user);
            log.info("User retrieved successfully: {}", user);
        } catch (UserNotFoundException e) {
            log.error("User not found with email {}: {}", email, e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    // ==================== UPDATE ====================
    private void updateUser(Context ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        log.info("PUT /api/v1/users/{} - Request to update user", id);
        User userUpdates = ctx.bodyAsClass(User.class);
        
        // Assicuriamoci che l'ID del corpo o del path sia coerente
        userUpdates.setId(id);

        try {
            User updatedUser = userService.updateUser(userUpdates);
            ctx.status(HttpStatus.OK).json(updatedUser);
            log.info("User updated successfully: {}", updatedUser);
        } catch (UserNotFoundException e) {
            log.error("User not found for update: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    // ==================== DELETE ====================
    private void deleteUserById(Context ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        log.info("DELETE /api/v1/users/{} - Request to delete user", id);
        try {
            userService.deleteUserById(id);
            ctx.status(HttpStatus.NO_CONTENT);
            log.info("User deleted successfully with ID: {}", id);
        } catch (UserNotFoundException e) {
            log.error("User not found for deletion: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void deleteAllUsers(Context ctx) {
        log.info("DELETE /api/v1/users - Request to delete all users");
        userService.deleteAllUsers();
        ctx.status(HttpStatus.NO_CONTENT);
        log.info("All users deleted successfully");
    }

    private void deleteUserByEmail(Context ctx) {
        String email = ctx.pathParam("email");
        log.info("DELETE /api/v1/users/email/{} - Request to delete user by email", email);
        try {
            userService.deleteUserByEmail(email);
            ctx.status(HttpStatus.NO_CONTENT);
            log.info("User deleted successfully with email: {}", email);
        } catch (UserNotFoundException e) {
            log.error("User not found for deletion with email {}: {}", email, e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }
}