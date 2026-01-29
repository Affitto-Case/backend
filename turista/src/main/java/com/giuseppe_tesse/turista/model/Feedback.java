package com.giuseppe_tesse.turista.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feedback {

    private Long id;
    private Long bookingId;
    private Long userId;
    private String title;
    private int rating;
    private String comment;

    public Feedback(Long bookingId,Long userId, String title, int rating, String comment) {
        this.bookingId = bookingId;
        this.userId=userId;
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
                ", booking=" + bookingId +
                ", title='" + title + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", userId='" + userId + '\''+
                '}';
    }
}
