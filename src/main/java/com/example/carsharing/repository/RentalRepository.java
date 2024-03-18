package com.example.carsharing.repository;

import com.example.carsharing.model.Rental;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    Page<Rental> findAllByUserId(Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM rentals r"
             + " WHERE r.actual_return_date IS NULL = :isActive", nativeQuery = true)
    Page<Rental> findAllByActivity(@Param("isActive") boolean isActive, Pageable pageable);

    @Query(value = "SELECT * FROM rentals r WHERE r.user_id = :userId"
             + " AND r.actual_return_date IS NULL = :isActive", nativeQuery = true)
    Page<Rental> findAllByUserAndActivity(@Param("userId") Long userId,
                                          @Param("isActive") boolean isActive,
                                          Pageable pageable);

    @Query(value = "SELECT * FROM rentals r WHERE r.actual_return_date IS NULL"
             + " AND DATE(r.return_date)  <= DATE(:deadline)", nativeQuery = true)
    List<Rental> findAllOverdue(@Param("deadline") LocalDate deadline);
}
