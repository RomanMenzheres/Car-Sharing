package com.example.carsharing.service;

import com.example.carsharing.dto.rental.CreateRentalRequestDto;
import com.example.carsharing.dto.rental.RentalDto;
import com.example.carsharing.dto.rental.RentalWithDetailedCarInfoDto;
import com.example.carsharing.model.User;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalWithDetailedCarInfoDto save(CreateRentalRequestDto requestDto, User user);

    List<RentalDto> findAllByUserAndActivity(Long userId, boolean isActive, Pageable pageable);

    List<RentalDto> findAllByUser(Long userId, Pageable pageable);

    RentalWithDetailedCarInfoDto returnRental(Long id);

    RentalWithDetailedCarInfoDto findById(Long id);

    void checkOverdueRentals();

    BigDecimal getAmountToPay(Long rentalId);
}
