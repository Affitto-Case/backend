package com.giuseppe_tesse.turista.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feedback {

    private Long id;
    private Booking booking;
    private User user;
    private String title;
    private int rating;
    private String comment;

    public Feedback(Booking booking,User user, String title, int rating, String comment) {
        this.booking = booking;
        this.user=user;
        this.title = title;
        this.rating = rating;
        this.comment = comment;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Feedback other = (Feedback) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", booking=" + booking +
                ", title='" + title + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                '}';
    }
}
