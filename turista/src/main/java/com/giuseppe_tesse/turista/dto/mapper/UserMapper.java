package com.giuseppe_tesse.turista.dto.mapper;

import com.giuseppe_tesse.turista.dto.request.UserRequestDTO;
import com.giuseppe_tesse.turista.dto.response.UserResponseDTO;
import com.giuseppe_tesse.turista.model.User;

public class UserMapper {

    // ==================== REQUEST DTO → ENTITY ====================
    
    /**
     * Converte UserRequestDTO in User entity
     * 
     * @param dto UserRequestDTO con i dati della richiesta
     * @return User entity (senza ID, sarà generato dal DB)
     */
    public static User toEntity(UserRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setAddress(dto.getAddress());
        
        return user;
    }

    // ==================== ENTITY → RESPONSE DTO ====================
    
    /**
     * Converte User entity in UserResponseDTO
     * 
     * @param user User entity da convertire
     * @return UserResponseDTO con tutti i dati
     */
    public static UserResponseDTO toResponseDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserResponseDTO dto = new UserResponseDTO();
        
        dto.setUserId(user.getId());
        dto.setUserFirstName(user.getFirstName());
        dto.setUserLastName(user.getLastName());
        dto.setUserEmail(user.getEmail());
        dto.setAddress(user.getAddress());
        dto.setRegistrationDate(user.getRegistrationDate());
        
        return dto;
    }

    // ==================== UTILITY METHODS ====================
    
    /**
     * Aggiorna un'entità User esistente con i dati del DTO
     * 
     * @param existingUser User entity esistente
     * @param dto UserRequestDTO con i nuovi dati
     * @return User entity aggiornata
     */
    public static User updateEntity(User existingUser, UserRequestDTO dto) {
        if (existingUser == null || dto == null) {
            return existingUser;
        }
        
        if (dto.getFirstName() != null) {
            existingUser.setFirstName(dto.getFirstName());
        }
        
        if (dto.getLastName() != null) {
            existingUser.setLastName(dto.getLastName());
        }
        
        if (dto.getEmail() != null) {
            existingUser.setEmail(dto.getEmail());
        }
        
        if (dto.getAddress() != null) {
            existingUser.setAddress(dto.getAddress());
        }
        
        // Password gestita separatamente nel Service se necessario
        
        return existingUser;
    }
    
    /**
     * Versione semplificata per liste
     */
    public static UserResponseDTO toResponseDTOSimple(User user) {
        if (user == null) {
            return null;
        }
        
        UserResponseDTO dto = new UserResponseDTO();
        
        dto.setUserId(user.getId());
        dto.setUserFirstName(user.getFirstName());
        dto.setUserLastName(user.getLastName());
        dto.setUserEmail(user.getEmail());
        
        return dto;
    }
    
    /**
     * Concatena firstName e lastName
     */
    public static String getFullName(User user) {
        if (user == null) {
            return "";
        }
        
        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName = user.getLastName() != null ? user.getLastName() : "";
        
        return (firstName + " " + lastName).trim();
    }
    
    /**
     * Previene istanziazione
     */
    private UserMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}