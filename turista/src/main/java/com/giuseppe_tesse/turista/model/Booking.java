package com.giuseppe_tesse.turista.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    private Long id;
    private Residence residence;
    private User user;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Booking(Residence residence,
                   User user,
                   LocalDateTime startDate,
                   LocalDateTime endDate) {

        this.residence = residence;
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking booking)) return false;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
