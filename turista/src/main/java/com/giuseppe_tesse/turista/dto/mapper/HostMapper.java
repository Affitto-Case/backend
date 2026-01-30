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
    
    /**
     * Previene istanziazione
     */
    private HostMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
