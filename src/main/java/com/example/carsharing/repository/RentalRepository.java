package com.example.carsharing.repository;

import com.example.carsharing.model.Rental;
import com.example.carsharing.model.User;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findAllByUser(User user, Pageable pageable);
}
