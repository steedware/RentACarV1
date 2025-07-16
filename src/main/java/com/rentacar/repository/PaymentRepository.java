package com.rentacar.repository;

import com.rentacar.model.Payment;
import com.rentacar.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReservation(Reservation reservation);
    Optional<Payment> findByTransactionId(String transactionId);
}
