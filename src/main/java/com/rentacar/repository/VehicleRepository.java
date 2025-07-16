package com.rentacar.repository;

import com.rentacar.model.Reservation;
import com.rentacar.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    List<Vehicle> findByAvailableTrue();
    
    List<Vehicle> findByType(Vehicle.VehicleType type);

    @Query("SELECT v FROM Vehicle v WHERE v.id NOT IN " +
           "(SELECT r.vehicle.id FROM Reservation r WHERE " +
           "((r.startDate <= :endDate AND r.endDate >= :startDate) AND r.status <> :excludeStatus))")
    List<Vehicle> findAvailableInTimeRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("excludeStatus") Reservation.ReservationStatus excludeStatus);
}
