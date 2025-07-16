package com.rentacar.exception;

public class ReservationException extends RuntimeException {

    private final Long vehicleId;
    
    public ReservationException(String message) {
        super(message);
        this.vehicleId = null;
    }
    
    public ReservationException(String message, Long vehicleId) {
        super(message);
        this.vehicleId = vehicleId;
    }
    
    public Long getVehicleId() {
        return vehicleId;
    }
}
