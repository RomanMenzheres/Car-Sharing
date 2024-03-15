package com.example.carsharing.repository;

import com.example.carsharing.model.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    Page<Rental> findAllByUserId(Long userId, Pageable pageable);
}
