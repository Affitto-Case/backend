package com.giuseppe_tesse.turista.dto.mapper;

import com.giuseppe_tesse.turista.dto.request.HostRequestDTO;
import com.giuseppe_tesse.turista.dto.response.HostResponseDTO;
import com.giuseppe_tesse.turista.model.Host;

public class HostMapper {

    // ==================== REQUEST DTO → ENTITY ====================
    
    /**
     * Converte HostRequestDTO in Host entity
     * 
     * @param dto HostRequestDTO con i dati della richiesta
     * @return Host entity (senza ID, sarà generato dal DB)
     */
    public static Host toEntity(HostRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Host host = new Host();
        
        host.setFirstName(dto.getFirstName());
        host.setLastName(dto.getLastName());
        host.setEmail(dto.getEmail());
        host.setAddress(dto.getAddress());
        
        host.setHost_code(dto.getHostCode());
        host.setTotal_bookings(0); 
        
        return host;
    }

    // ==================== ENTITY → RESPONSE DTO ====================
    
    /**
     * Converte Host entity in HostResponseDTO
     * Include il flag isSuperHost calcolato
     * 
     * @param host Host entity da convertire
     * @return HostResponseDTO con tutti i dati
     */
    public static HostResponseDTO toResponseDTO(Host host) {
        if (host == null) {
            return null;
        }
        
        HostResponseDTO dto = new HostResponseDTO();
        
        dto.setId(host.getId());
        
        dto.setFirstName(host.getFirstName());
        dto.setLastName(host.getLastName());
        dto.setEmail(host.getEmail());
        dto.setAddress(host.getAddress());
        dto.setRegistrationDate(host.getRegistrationDate());
        
        dto.setHostCode(host.getHost_code());
        dto.setTotalBookings(host.getTotal_bookings());
        dto.setIsSuperHost(host.isSuperHost()); 
        
        return dto;
    }

    // ==================== UTILITY METHODS ====================
    
    /**
     * Aggiorna un'entità Host esistente con i dati del DTO
     * 
     * @param existingHost Host entity esistente
     * @param dto HostRequestDTO con i nuovi dati
     * @return Host entity aggiornata
     */
    public static Host updateEntity(Host existingHost, HostRequestDTO dto) {
        if (existingHost == null || dto == null) {
            return existingHost;
        }
        
        if (dto.getFirstName() != null) {
            existingHost.setFirstName(dto.getFirstName());
        }
        
        if (dto.getLastName() != null) {
            existingHost.setLastName(dto.getLastName());
        }
        
        if (dto.getEmail() != null) {
            existingHost.setEmail(dto.getEmail());
        }
        
        if (dto.getAddress() != null) {
            existingHost.setAddress(dto.getAddress());
        }
        
        
        if (dto.getHostCode() != null) {
            existingHost.setHost_code(dto.getHostCode());
        }
        
        return existingHost;
    }
    
    /**
     * Versione semplificata per liste
     */
    public static HostResponseDTO toResponseDTOSimple(Host host) {
        if (host == null) {
            return null;
        }
        
        HostResponseDTO dto = new HostResponseDTO();
        
        dto.setId(host.getId());
        dto.setFirstName(host.getFirstName());
        dto.setLastName(host.getLastName());
        dto.setHostCode(host.getHost_code());
        dto.setTotalBookings(host.getTotal_bookings());
        dto.setIsSuperHost(host.isSuperHost());
        
        return dto;
    }
    
    /**
     * Previene istanziazione
     */
    private HostMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
