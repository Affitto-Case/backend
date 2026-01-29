package com.giuseppe_tesse.turista.controller;

import java.util.List;

import com.giuseppe_tesse.turista.exception.BookingNotFoundException;
import com.giuseppe_tesse.turista.exception.DuplicateBookingException;
import com.giuseppe_tesse.turista.model.Booking;
import com.giuseppe_tesse.turista.service.BookingService;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingController implements Controller {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    public void registerRoutes(Javalin app) {
        app.post("/api/v1/bookings", this::createBooking);
        app.get("/api/v1/bookings", this::getAllBookings);
        app.get("/api/v1/bookings/{id}", this::getBookingById);
        app.get("/api/v1/bookings/residence/{residenceId}", this::getBookingsByResidenceId);
        app.put("/api/v1/bookings/{id}", this::updateBooking);
        app.delete("/api/v1/bookings/{id}", this::deleteBookingById);
        app.delete("/api/v1/bookings", this::deleteAllBookings);
    }

    // ==================== CREATE ====================
    private void createBooking(Context ctx) {
        log.info("POST /api/v1/bookings - Request to create booking");
        Booking booking = ctx.bodyAsClass(Booking.class);

        try {
            Booking createdBooking = bookingService.createBooking(
                    booking.getResidenceId(),
                    booking.getUserId(),
                    booking.getStartDate(),
                    booking.getEndDate()
            );
            ctx.status(HttpStatus.CREATED).json(createdBooking);
            log.info("Booking created successfully: {}", createdBooking);
        } catch (DuplicateBookingException e) {
            log.error("Error creating booking: {}", e.getMessage());
            ctx.status(HttpStatus.CONFLICT).result(e.getMessage());
        }
    }

    // ==================== READ ====================
    private void getAllBookings(Context ctx) {
        log.info("GET /api/v1/bookings - Request to fetch all bookings");
        List<Booking> bookings = bookingService.getAllBookings();
        ctx.status(HttpStatus.OK).json(bookings);
        log.info("All bookings retrieved successfully, total: {}", bookings.size());
    }

    private void getBookingById(Context ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        log.info("GET /api/v1/bookings/{} - Request to fetch booking by ID", id);
        try {
            Booking booking = bookingService.getBookingById(id);
            ctx.status(HttpStatus.OK).json(booking);
            log.info("Booking retrieved successfully: {}", booking);
        } catch (BookingNotFoundException e) {
            log.error("Booking not found: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void getBookingsByResidenceId(Context ctx) {
        Long residenceId = Long.valueOf(ctx.pathParam("residenceId"));
        log.info("GET /api/v1/bookings/residence/{} - Request to fetch bookings by residence ID", residenceId);
        try {
            List<Booking> bookings = bookingService.getBookingsByResidenceId(residenceId);
            ctx.status(HttpStatus.OK).json(bookings);
            log.info("Bookings retrieved successfully for residence ID {}, total: {}", residenceId, bookings.size());
        } catch (BookingNotFoundException e) {
            log.error("Bookings not found for residence ID {}: {}", residenceId, e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    // ==================== UPDATE ====================
    private void updateBooking(Context ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        log.info("PUT /api/v1/bookings/{} - Request to update booking", id);
        Booking bookingUpdates = ctx.bodyAsClass(Booking.class);

        try {
            // Recupero l'oggetto esistente per poi aggiornarlo
            Booking existingBooking = bookingService.getBookingById(id);
            existingBooking.setStartDate(bookingUpdates.getStartDate());
            existingBooking.setEndDate(bookingUpdates.getEndDate());

            Booking updatedBooking = bookingService.updateBooking(existingBooking);
            ctx.status(HttpStatus.OK).json(updatedBooking);
            log.info("Booking updated successfully: {}", updatedBooking);
        } catch (BookingNotFoundException e) {
            log.error("Error updating booking: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    // ==================== DELETE ====================
    private void deleteBookingById(Context ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        log.info("DELETE /api/v1/bookings/{} - Request to delete booking", id);
        try {
            bookingService.deleteBookingById(id);
            ctx.status(HttpStatus.NO_CONTENT);
            log.info("Booking deleted successfully with ID: {}", id);
        } catch (BookingNotFoundException e) {
            log.error("Error deleting booking: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void deleteAllBookings(Context ctx) {
        log.info("DELETE /api/v1/bookings - Request to delete all bookings");
        bookingService.deleteAllBookings();
        ctx.status(HttpStatus.NO_CONTENT);
        log.info("All bookings deleted successfully");
    }
}