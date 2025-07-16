package com.rentacar.repository;

import com.rentacar.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.reservations WHERE u.email = :email")
    Optional<User> findByEmailWithReservations(@Param("email") String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.reservations WHERE u.id = :id")
    Optional<User> findByIdWithReservations(@Param("id") Long id);
}
