package com.example.carsharing.repository;

import com.example.carsharing.model.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findBySessionId(String sessionId);

    Optional<Payment> findByRentalId(Long rentalId);
}
