package com.giuseppe_tesse.turista.model;

import java.time.LocalDate;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Host extends User {

    private String host_code;
    private int total_bookings;
    private boolean isSuperHost;

    public Host(String first_name,
                String last_name,
                String email,
                String password,
                String address,
                LocalDate registration_date,
                String host_code,
                int total_bookings) {

        super(first_name, last_name, email, password, address, registration_date);
        this.host_code = host_code;
        this.total_bookings = total_bookings;
        isSuperHost=false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(host_code);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Host other = (Host) obj;
        return Objects.equals(this.host_code, other.host_code);
    }

    @Override
    public String toString() {
        return super.toString() +
                " Host [host_code=" + host_code +
                ", total_bookings=" + total_bookings + "]";
    }
}
