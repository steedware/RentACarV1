package com.rentacar.repository;

import com.rentacar.model.Reservation;
import com.rentacar.model.Reservation.ReservationStatus;
import com.rentacar.model.User;
import com.rentacar.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    List<Reservation> findByUser(User user);
    
    List<Reservation> findAllByVehicle(Vehicle vehicle);
    
    @Query("SELECT r FROM Reservation r WHERE " +
           "r.vehicle = :vehicle AND " +
           "r.status <> :canceledStatus AND " +
           "((r.startDate <= :endDate AND r.endDate >= :startDate))")
    List<Reservation> findOverlappingReservations(
            @Param("vehicle") Vehicle vehicle,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("canceledStatus") ReservationStatus canceledStatus);
}