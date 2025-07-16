package com.rentacar.exception;

public class PaymentException extends RuntimeException {

    private final Long reservationId;
    
    public PaymentException(String message) {
        super(message);
        this.reservationId = null;
    }
    
    public PaymentException(String message, Long reservationId) {
        super(message);
        this.reservationId = reservationId;
    }
    
    public Long getReservationId() {
        return reservationId;
    }
}
