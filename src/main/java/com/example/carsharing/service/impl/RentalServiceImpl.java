package com.example.carsharing.service.impl;

import com.example.carsharing.dto.rental.CreateRentalRequestDto;
import com.example.carsharing.dto.rental.RentalDto;
import com.example.carsharing.dto.rental.RentalWithDetailedCarInfoDto;
import com.example.carsharing.exception.EntityNotFoundException;
import com.example.carsharing.exception.RentalIsNotActiveException;
import com.example.carsharing.mapper.RentalMapper;
import com.example.carsharing.model.Car;
import com.example.carsharing.model.Rental;
import com.example.carsharing.model.User;
import com.example.carsharing.repository.CarRepository;
import com.example.carsharing.repository.RentalRepository;
import com.example.carsharing.service.CarService;
import com.example.carsharing.service.NotificationService;
import com.example.carsharing.service.RentalService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RentalServiceImpl implements RentalService {
    private static final BigDecimal FINE_MULTIPLIER = BigDecimal.valueOf(1.5);
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarService carService;
    private final CarRepository carRepository;
    private final NotificationService notificationService;

    @Override
    public RentalWithDetailedCarInfoDto create(CreateRentalRequestDto requestDto, User user) {
        long carId = requestDto.carId();
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find car with id: " + carId));
        carService.rentalCar(car);
        Rental rental = rentalMapper.toModel(requestDto);
        rental.setUser(user);
        rental.setCar(car);
        rental.setRentalDate(LocalDate.now());
        RentalWithDetailedCarInfoDto rentalDto =
                rentalMapper.toWithDetailedCarInfoDto(rentalRepository.save(rental));
        notificationService.onRentalCreationNotification(rentalDto);
        return rentalDto;
    }

    @Override
    public List<RentalDto> findAllBy(Long userId, boolean isActive, Pageable pageable) {
        if (userId == null) {
            return rentalRepository.findAll(pageable).stream()
                    .filter(rental -> (rental.getActualReturnDate() == null) == isActive)
                    .map(rentalMapper::toDto)
                    .toList();
        }

        return findAllByUser(new User(userId), pageable).stream()
                .filter(rentalDto -> (rentalDto.actualReturnDate() == null) == isActive)
                .toList();
    }

    @Override
    public RentalWithDetailedCarInfoDto returnRental(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find rental with id: " + id));

        if (rental.getActualReturnDate() != null) {
            throw new RentalIsNotActiveException("This rental is already not active");
        }
        carService.returnRentalCar(rental.getCar());
        rental.setActualReturnDate(LocalDate.now());
        return rentalMapper.toWithDetailedCarInfoDto(rentalRepository.save(rental));
    }

    @Override
    public RentalWithDetailedCarInfoDto findById(Long id) {
        return rentalMapper.toWithDetailedCarInfoDto(rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find rental with id " + id)));
    }

    @Override
    public List<RentalDto> findAllByUser(User user, Pageable pageable) {
        return rentalRepository.findAllByUser(user, pageable).stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    @Scheduled(cron = "0 0 8 * * *")
    public void checkOverdueRentals() {
        LocalDate deadline = LocalDate.now().plusDays(1);
        List<RentalDto> overdueRentals = rentalRepository.findAll().stream()
                .filter(rental -> rental.getActualReturnDate() == null)
                .filter(rental -> deadline.isAfter(rental.getReturnDate())
                        || deadline.isEqual(rental.getReturnDate()))
                .map(rentalMapper::toDto)
                .toList();
        notificationService.scheduledOverdueRentalNotification(overdueRentals);
    }

    @Override
    public BigDecimal getAmountToPay(Rental rental) {
        BigDecimal dailyFee = rental.getCar().getDailyFee();
        LocalDate rentalDate = rental.getRentalDate();
        LocalDate returnDate = rental.getReturnDate();
        LocalDate actualReturnDate = rental.getActualReturnDate();

        if (actualReturnDate != null) {
            return dailyFee.multiply(BigDecimal.valueOf(
                    ChronoUnit.DAYS.between(rentalDate, actualReturnDate))
            );
        }

        BigDecimal rentalDays = BigDecimal.valueOf(
                ChronoUnit.DAYS.between(rentalDate, returnDate)
        );

        if (returnDate.isBefore(LocalDate.now())) {
            BigDecimal penaltyDays = BigDecimal.valueOf(
                    ChronoUnit.DAYS.between(returnDate, LocalDate.now())
            );

            return dailyFee
                    .multiply(rentalDays)
                    .add(dailyFee
                            .multiply(FINE_MULTIPLIER)
                            .multiply(penaltyDays));
        }

        return dailyFee.multiply(rentalDays);
    }
}
