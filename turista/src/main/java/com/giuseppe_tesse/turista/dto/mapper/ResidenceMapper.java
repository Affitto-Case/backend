package com.giuseppe_tesse.turista.dto.mapper;

import com.giuseppe_tesse.turista.dto.request.ResidenceRequestDTO;
import com.giuseppe_tesse.turista.dto.response.ResidenceResponseDTO;
import com.giuseppe_tesse.turista.model.Host;
import com.giuseppe_tesse.turista.model.Residence;

import java.time.LocalDate;

public class ResidenceMapper {

    // ==================== REQUEST DTO → ENTITY ====================
    
    /**
     * Converte ResidenceRequestDTO in Residence entity
     * 
     * @param dto ResidenceRequestDTO con i dati della richiesta
     * @param host Host proprietario della residenza
     * @return Residence entity (senza ID, sarà generato dal DB)
     */
    public static Residence toEntity(ResidenceRequestDTO dto, Host host) {
        if (dto == null) {
            return null;
        }
        
        Residence residence = new Residence();
        residence.setName(dto.getName());
        residence.setAddress(dto.getAddress());
        residence.setPrice_per_night(dto.getPricePerNight());
        residence.setNumber_of_rooms(dto.getNumberOfRooms());
        residence.setGuest_capacity(dto.getGuestCapacity());
        residence.setFloor(dto.getFloor());
        residence.setAvailable_from(dto.getAvailableFrom());
        residence.setAvailable_to(dto.getAvailableTo());
        residence.setHost(host);
        
        return residence;
    }

    // ==================== ENTITY → RESPONSE DTO ====================
    
    /**
     * Converte Residence entity in ResidenceResponseDTO
     * Include informazioni sull'Host proprietario
     * 
     * @param residence Residence entity da convertire
     * @return ResidenceResponseDTO con tutti i dati
     */
    public static ResidenceResponseDTO toResponseDTO(Residence residence) {
        if (residence == null) {
            return null;
        }
        
        ResidenceResponseDTO dto = new ResidenceResponseDTO();
        
        dto.setId(residence.getId());
        dto.setName(residence.getName());
        dto.setAddress(residence.getAddress());
        dto.setPricePerNight(residence.getPrice_per_night());
        dto.setNumberOfRooms(residence.getNumber_of_rooms());
        dto.setGuestCapacity(residence.getGuest_capacity());
        dto.setFloor(residence.getFloor());
        dto.setAvailableFrom(residence.getAvailable_from());
        dto.setAvailableTo(residence.getAvailable_to());
        
        if (residence.getHost() != null) {
            Host host = residence.getHost();
            dto.setHostId(host.getId());
            dto.setHostName(host.getFirstName() + " " + host.getLastName());
            dto.setHostEmail(host.getEmail());
            dto.setHostCode(host.getHost_code());
        }
        
        return dto;
    }

    // ==================== UTILITY METHODS ====================
    
    /**
     * Aggiorna un'entità Residence esistente con i dati del DTO
     * 
     * @param existingResidence Residence entity esistente
     * @param dto ResidenceRequestDTO con i nuovi dati
     * @return Residence entity aggiornata
     */
    public static Residence updateEntity(Residence existingResidence, ResidenceRequestDTO dto) {
        if (existingResidence == null || dto == null) {
            return existingResidence;
        }
        
        if (dto.getName() != null) {
            existingResidence.setName(dto.getName());
        }
        
        if (dto.getAddress() != null) {
            existingResidence.setAddress(dto.getAddress());
        }
        
        if (dto.getPricePerNight() != null) {
            existingResidence.setPrice_per_night(dto.getPricePerNight());
        }
        
        if (dto.getNumberOfRooms() != null) {
            existingResidence.setNumber_of_rooms(dto.getNumberOfRooms());
        }
        
        if (dto.getGuestCapacity() != null) {
            existingResidence.setGuest_capacity(dto.getGuestCapacity());
        }
        
        if (dto.getFloor() != null) {
            existingResidence.setFloor(dto.getFloor());
        }
        
        if (dto.getAvailableFrom() != null) {
            existingResidence.setAvailable_from(dto.getAvailableFrom());
        }
        
        if (dto.getAvailableTo() != null) {
            existingResidence.setAvailable_to(dto.getAvailableTo());
        }
        
        return existingResidence;
    }
    
    /**
     * Versione semplificata per liste
     */
    public static ResidenceResponseDTO toResponseDTOSimple(Residence residence) {
        if (residence == null) {
            return null;
        }
        
        ResidenceResponseDTO dto = new ResidenceResponseDTO();
        
        dto.setId(residence.getId());
        dto.setName(residence.getName());
        dto.setAddress(residence.getAddress());
        dto.setPricePerNight(residence.getPrice_per_night());
        dto.setGuestCapacity(residence.getGuest_capacity());
        
        if (residence.getHost() != null) {
            dto.setHostName(residence.getHost().getFirstName() + " " + residence.getHost().getLastName());
        }
        
        return dto;
    }
    
    /**
     * Verifica se la residence è disponibile in una certa data
     */
    public static boolean isAvailableOn(Residence residence, LocalDate date) {
        if (residence == null || date == null) {
            return false;
        }
        
        LocalDate from = residence.getAvailable_from();
        LocalDate to = residence.getAvailable_to();
        
        if (from == null || to == null) {
            return false;
        }
        
        return !date.isBefore(from) && !date.isAfter(to);
    }
    
    /**
     * Previene istanziazione
     */
    private ResidenceMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
