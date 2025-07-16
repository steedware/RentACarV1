package com.rentacar.service;

import com.rentacar.model.Reservation;
import com.rentacar.model.User;
import com.rentacar.model.Vehicle;
import com.rentacar.repository.ReservationRepository;
import com.rentacar.repository.VehicleRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final VehicleRepository vehicleRepository;

    // Add explicit constructor to make autowiring more clear
    public ReservationService(ReservationRepository reservationRepository, VehicleRepository vehicleRepository) {
        this.reservationRepository = reservationRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public List<Reservation> getReservationsByUser(User user) {
        return reservationRepository.findByUser(user);
    }

    public List<Reservation> getReservationsByVehicle(Vehicle vehicle) {
        return reservationRepository.findAllByVehicle(vehicle);
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    @Transactional
    public Reservation createReservation(User user, Vehicle vehicle, LocalDateTime startDate, LocalDateTime endDate) {
        // Check if vehicle is available during the specified time range
        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(
                vehicle, startDate, endDate, Reservation.ReservationStatus.CANCELED);
        
        if (!overlappingReservations.isEmpty()) {
            throw new IllegalStateException("Vehicle is not available during the specified time range");
        }

        // Calculate total cost
        long days = Duration.between(startDate, endDate).toDays();
        if (days < 1) days = 1; // Minimum 1 day
        
        BigDecimal totalCost = vehicle.getDailyRate().multiply(BigDecimal.valueOf(days));
        
        // Create and save reservation
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setVehicle(vehicle);
        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);
        reservation.setTotalCost(totalCost);
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        
        reservation.setStatus(Reservation.ReservationStatus.CANCELED);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation completeReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        
        reservation.setStatus(Reservation.ReservationStatus.COMPLETED);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation rateReservation(Long reservationId, int rating, String feedback) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        reservation.setRating(rating);
        reservation.setFeedback(feedback);
        
        // Update vehicle rating
        Vehicle vehicle = reservation.getVehicle();
        int currentRatingCount = vehicle.getRatingCount() != null ? vehicle.getRatingCount() : 0;
        double currentRating = vehicle.getRating() != null ? vehicle.getRating() : 0.0;
        
        // Calculate new average rating
        double newRating = ((currentRating * currentRatingCount) + rating) / (currentRatingCount + 1);
        
        vehicle.setRatingCount(currentRatingCount + 1);
        vehicle.setRating(newRating);
        vehicleRepository.save(vehicle);
        
        return reservationRepository.save(reservation);
    }
}
