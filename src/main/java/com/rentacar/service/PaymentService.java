package com.rentacar.service;

import com.rentacar.model.Payment;
import com.rentacar.model.Reservation;
import com.rentacar.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationService reservationService;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public PaymentIntent createPaymentIntent(Reservation reservation) throws StripeException {
        // Convert BigDecimal to cents (long) for Stripe
        long amount = reservation.getTotalCost().multiply(BigDecimal.valueOf(100)).longValue();
        
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setCurrency("pln")
                .setAmount(amount)
                .setDescription("Reservation #" + reservation.getId())
                .putMetadata("reservation_id", reservation.getId().toString())
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .build()
                )
                .build();
                
        return PaymentIntent.create(params);
    }

    @Transactional
    public Payment processPayment(Reservation reservation, String paymentIntentId) {
        // Record the payment
        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setTransactionId(paymentIntentId);
        payment.setAmount(reservation.getTotalCost());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setMethod(Payment.PaymentMethod.CREDIT_CARD);
        
        // Update reservation status
        reservationService.confirmReservation(reservation.getId());
        
        return paymentRepository.save(payment);
    }

    public Optional<Payment> getPaymentByReservation(Reservation reservation) {
        return paymentRepository.findByReservation(reservation);
    }

    @Transactional
    public Payment refundPayment(String paymentIntentId) throws StripeException {
        // First, retrieve the payment from our database
        Optional<Payment> paymentOpt = paymentRepository.findByTransactionId(paymentIntentId);
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found with ID: " + paymentIntentId);
        }
        
        Payment payment = paymentOpt.get();
        
        // Process refund through Stripe
        Map<String, Object> params = new HashMap<>();
        params.put("payment_intent", paymentIntentId);
        
        com.stripe.model.Refund.create(params);
        
        // Update our payment record
        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        
        // Cancel the reservation
        reservationService.cancelReservation(payment.getReservation().getId());
        
        return paymentRepository.save(payment);
    }
}
