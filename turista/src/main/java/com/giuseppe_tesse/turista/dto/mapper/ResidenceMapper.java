package com.giuseppe_tesse.turista.dto.mapper;

import com.giuseppe_tesse.turista.dto.request.ResidenceRequestDTO;
import com.giuseppe_tesse.turista.dto.response.ResidenceResponseDTO;
import com.giuseppe_tesse.turista.model.Host;
import com.giuseppe_tesse.turista.model.Residence;

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
        residence.setPrice_per_night(dto.getPrice());
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
        dto.setPrice(residence.getPrice_per_night());
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
    
    /**
     * Previene istanziazione
     */
    private ResidenceMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
