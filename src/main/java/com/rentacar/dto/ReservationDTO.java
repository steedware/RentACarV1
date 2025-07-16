package com.rentacar.dto;

import com.rentacar.model.Reservation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {

    private Long id;
    private Long userId;
    private String userName;
    private Long vehicleId;
    private String vehicleName;
    private String vehicleImageUrl;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal totalCost;
    private Reservation.ReservationStatus status;
    private String stripePaymentId;
    private Integer rating;
    private String feedback;

    // Constructor from entity
    public ReservationDTO(Reservation reservation) {
        this.id = reservation.getId();
        
        if (reservation.getUser() != null) {
            this.userId = reservation.getUser().getId();
            this.userName = reservation.getUser().getFirstName() + " " + reservation.getUser().getLastName();
        }
        
        if (reservation.getVehicle() != null) {
            this.vehicleId = reservation.getVehicle().getId();
            this.vehicleName = reservation.getVehicle().getBrand() + " " + reservation.getVehicle().getModel();
            this.vehicleImageUrl = reservation.getVehicle().getImageUrl();
        }
        
        this.startDate = reservation.getStartDate();
        this.endDate = reservation.getEndDate();
        this.totalCost = reservation.getTotalCost();
        this.status = reservation.getStatus();
        this.stripePaymentId = reservation.getStripePaymentId();
        this.rating = reservation.getRating();
        this.feedback = reservation.getFeedback();
    }
}
