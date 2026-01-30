package com.giuseppe_tesse.turista.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.giuseppe_tesse.turista.dto.mapper.BookingMapper;
import com.giuseppe_tesse.turista.dto.request.BookingRequestDTO;
import com.giuseppe_tesse.turista.dto.response.BookingResponseDTO;
import com.giuseppe_tesse.turista.exception.BookingNotFoundException;
import com.giuseppe_tesse.turista.exception.DuplicateBookingException;
import com.giuseppe_tesse.turista.exception.ResidenceNotFoundException;
import com.giuseppe_tesse.turista.exception.UserNotFoundException;
import com.giuseppe_tesse.turista.model.Booking;
import com.giuseppe_tesse.turista.model.Residence;
import com.giuseppe_tesse.turista.model.User;
import com.giuseppe_tesse.turista.service.BookingService;
import com.giuseppe_tesse.turista.service.ResidenceService;
import com.giuseppe_tesse.turista.service.UserService;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingController implements Controller {

    private final BookingService bookingService;
    private final ResidenceService residenceService;
    private final UserService userService;
    
    public BookingController(BookingService bookingService,ResidenceService residenceService,UserService userService) {
        this.bookingService = bookingService;
        this.residenceService = residenceService;
        this.userService = userService;
    }

    @Override
    public void registerRoutes(Javalin app) {
        app.post("/api/v1/bookings", this::createBooking);
        app.get("/api/v1/bookings", this::getAllBookings);
        app.get("/api/v1/bookings/{id}", this::getBookingById);
        app.get("/api/v1/bookings/residence/{residenceId}", this::getBookingsByResidenceId);
        app.get("/api/v1/bookings/user/{userId}/last",this::getLastBookingByUserId);
        app.put("/api/v1/bookings/{id}", this::updateBooking);
        app.delete("/api/v1/bookings/{id}", this::deleteBookingById);
        app.delete("/api/v1/bookings", this::deleteAllBookings);
    }

    // ==================== CREATE ====================
    private void createBooking(Context ctx) {
        log.info("POST /api/v1/bookings - Request to create booking");
        try {
            BookingRequestDTO requestDTO = ctx.bodyAsClass(BookingRequestDTO.class);
            Residence residence = residenceService.getResidenceById(requestDTO.getResidenceId());
            User user = userService.getUserById(requestDTO.getUserId());
            Booking booking = BookingMapper.toEntity(requestDTO, user, residence);
            Booking createdBooking = bookingService.createBooking(booking);
            BookingResponseDTO responseDTO = BookingMapper.toResponseDTO(createdBooking);
            ctx.status(HttpStatus.CREATED).json(responseDTO);
            log.info("Booking created successfully: {}", createdBooking.getId());
        } catch (ResidenceNotFoundException | UserNotFoundException e) {
            log.error("Error creating booking: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        } catch (DuplicateBookingException e) {
            log.error("Error creating booking: {}", e.getMessage());
            ctx.status(HttpStatus.CONFLICT).result(e.getMessage());
        } catch (Exception e){
            log.error("Error creating booking: {}", e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }

    // ==================== READ ====================
    private void getAllBookings(Context ctx) {
        log.info("GET /api/v1/bookings - Request to fetch all bookings");
        List<Booking> bookings = bookingService.getAllBookings();
        List<BookingResponseDTO> responseDTOs = bookings.stream()
                .map(BookingMapper::toResponseDTO)
                .collect(Collectors.toList());
        ctx.status(HttpStatus.OK).json(responseDTOs);
        log.info("All bookings retrieved successfully, total: {}", responseDTOs.size());
    }

    

    private void getBookingById(Context ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        log.info("GET /api/v1/bookings/{} - Request to fetch booking by ID", id);
        try {
            Booking booking = bookingService.getBookingById(id);
            BookingResponseDTO responseDTO = BookingMapper.toResponseDTO(booking);
            ctx.status(HttpStatus.OK).json(responseDTO);
            log.info("Booking retrieved successfully: {}", id);
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
            List<BookingResponseDTO> responseDTOs = bookings.stream()
                    .map(BookingMapper::toResponseDTO)
                    .collect(Collectors.toList());
            ctx.status(HttpStatus.OK).json(responseDTOs);
            log.info("Bookings retrieved successfully for residence ID {}, total: {}", residenceId, responseDTOs.size());
        } catch (BookingNotFoundException e) {
            log.error("Bookings not found for residence ID {}: {}", residenceId, e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void getLastBookingByUserId(Context ctx) {
        Long id = Long.valueOf(ctx.pathParam("userId"));
        log.info("GET /api/v1/bookings/user/{userId}/last - Request to fetch last booking by user ID", id);
        try {
            Booking booking = bookingService.getLastBookingByUserId(id);
            BookingResponseDTO responseDTO = BookingMapper.toResponseDTO(booking);
            ctx.status(HttpStatus.OK).json(responseDTO);
            log.info("Booking retrieved successfully: {}", id);
        } catch (BookingNotFoundException e) {
            log.error("Booking not found: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    // ==================== UPDATE ====================
    private void updateBooking(Context ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        log.info("PUT /api/v1/bookings/{} - Request to update booking", id);

        try {
            // Recupero l'oggetto esistente per poi aggiornarlo
            BookingRequestDTO requestDTO = ctx.bodyAsClass(BookingRequestDTO.class);

            Booking existingBooking = bookingService.getBookingById(id);
            existingBooking.setStartDate(requestDTO.getStartDate());
            existingBooking.setEndDate(requestDTO.getEndDate());
            Booking updatedBooking = bookingService.updateBooking(existingBooking);
            BookingResponseDTO responseDTO = BookingMapper.toResponseDTO(updatedBooking);
            ctx.status(HttpStatus.OK).json(responseDTO);
            log.info("Booking updated successfully: {}", id);
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