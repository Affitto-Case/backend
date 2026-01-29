package com.giuseppe_tesse.turista.model;

import java.time.LocalDate;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Residence {

    private Long id;
    private String name;
    private String address;
    private double price_per_night;
    private int number_of_rooms;
    private int guest_capacity;
    private int floor;
    private LocalDate available_from;
    private LocalDate available_to;
    private Long hostId;

    // Costruttore senza id (per INSERT)
    public Residence(String name,
                     String address,
                     double price_per_night,
                     int number_of_rooms,
                     int guest_capacity,
                     int floor,
                     LocalDate available_from,
                     LocalDate available_to,
                     Long hostId) {

        this.name = name;
        this.address = address;
        this.price_per_night = price_per_night;
        this.number_of_rooms = number_of_rooms;
        this.guest_capacity = guest_capacity;
        this.floor = floor;
        this.available_from = available_from;
        this.available_to = available_to;
        this.hostId = hostId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Residence residence)) return false;
        return Objects.equals(id, residence.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Residence{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", price_per_night=" + price_per_night +
                ", number_of_rooms=" + number_of_rooms +
                ", guest_capacity=" + guest_capacity +
                ", floor=" + floor +
                ", available_from=" + available_from +
                ", available_to=" + available_to +
                ", hostId=" + hostId +
                '}';
    }
}
