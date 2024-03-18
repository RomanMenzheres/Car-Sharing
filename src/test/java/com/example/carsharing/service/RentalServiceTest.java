package com.example.carsharing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
import com.example.carsharing.service.impl.RentalServiceImpl;
import com.example.carsharing.supplier.CarSupplier;
import com.example.carsharing.supplier.RentalSupplier;
import com.example.carsharing.supplier.UserSupplier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {
    private static final BigDecimal FINE_MULTIPLIER = BigDecimal.valueOf(1.5);
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private CarService carService;
    @Mock
    private CarRepository carRepository;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private RentalServiceImpl rentalService;

    @Test
    @DisplayName("Verify save() method works")
    public void save_ValidCreateRequestDto_ReturnsRentalDto() {
        CreateRentalRequestDto requestDto =
                RentalSupplier.getCreateRentalRequestDto();
        RentalWithDetailedCarInfoDto expected =
                RentalSupplier.getRentalWithDetailedCarInfoDtoWithId4();
        Rental rental = RentalSupplier.getRental();
        User user = UserSupplier.getUser();
        Car car = CarSupplier.getCar();

        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(rentalMapper.toModel(requestDto)).thenReturn(rental);
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(rentalMapper.toWithDetailedCarInfoDto(rental)).thenReturn(expected);

        RentalWithDetailedCarInfoDto actual = rentalService.save(requestDto, user);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(carRepository, times(1)).findById(car.getId());
        verify(rentalMapper, times(1)).toModel(requestDto);
        verify(rentalRepository, times(1)).save(rental);
        verify(rentalMapper, times(1)).toWithDetailedCarInfoDto(rental);
        verify(notificationService, times(1)).onRentalCreationNotification(expected);
        verifyNoMoreInteractions(rentalRepository, rentalMapper,
                carRepository, notificationService);
    }

    @Test
    @DisplayName("Verify save() method throws exception when car is not exists")
    public void save_InvalidCreateRequestDto_ThrowsException() {
        CreateRentalRequestDto requestDto = RentalSupplier.getCreateRentalRequestDto();
        User user = UserSupplier.getUser();
        Car car = CarSupplier.getCar();

        when(carRepository.findById(car.getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> rentalService.save(requestDto, user)
        );

        String expected = "Can't find car with id: " + car.getId();
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(carRepository, times(1)).findById(car.getId());
        verifyNoMoreInteractions(carRepository);
    }

    @Test
    @DisplayName("Verify findAllByActivity() method works")
    public void findAllByActivity_ValidData_ReturnsAllRentals() {
        boolean isActive = true;
        Rental rental = RentalSupplier.getRental();
        RentalDto rentalDto = RentalSupplier.getRentalDto();
        Pageable pageable = PageRequest.of(0, 10);
        List<Rental> rentals = List.of(rental);
        Page<Rental> rentalPage = new PageImpl<>(rentals, pageable, rentals.size());

        when(rentalRepository.findAllByActivity(isActive, pageable)).thenReturn(rentalPage);
        when(rentalMapper.toDto(rental)).thenReturn(rentalDto);

        List<RentalDto> actual = rentalService.findAllByActivity(isActive, pageable);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0)).isEqualTo(rentalDto);
        verify(rentalRepository, times(1)).findAllByActivity(isActive, pageable);
        verify(rentalMapper, times(1)).toDto(rental);
        verifyNoMoreInteractions(rentalRepository, rentalMapper);
    }

    @Test
    @DisplayName("Verify findAllByUser() method works")
    public void findAll_ValidPageable_ReturnsAllRentals() {
        Long userId = 1L;
        Rental rental = RentalSupplier.getRental();
        RentalDto rentalDto = RentalSupplier.getRentalDto();
        Pageable pageable = PageRequest.of(0, 10);
        List<Rental> rentals = List.of(rental);
        Page<Rental> rentalPage = new PageImpl<>(rentals, pageable, rentals.size());

        when(rentalRepository.findAllByUserId(userId, pageable)).thenReturn(rentalPage);
        when(rentalMapper.toDto(rental)).thenReturn(rentalDto);

        List<RentalDto> actual = rentalService.findAllByUser(userId, pageable);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0)).isEqualTo(rentalDto);
        verify(rentalRepository, times(1)).findAllByUserId(userId, pageable);
        verify(rentalMapper, times(1)).toDto(rental);
        verifyNoMoreInteractions(rentalRepository, rentalMapper);
    }

    @Test
    @DisplayName("Verify the correct rental was returned when rental exists")
    public void findById_ValidId_ReturnsRentalDto() {
        Long rentalId = 4L;
        Rental rental = RentalSupplier.getRental();
        RentalWithDetailedCarInfoDto expected =
                RentalSupplier.getRentalWithDetailedCarInfoDtoWithId4();

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(rentalMapper.toWithDetailedCarInfoDto(rental)).thenReturn(expected);

        RentalWithDetailedCarInfoDto actual = rentalService.findById(rentalId);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(rentalRepository, times(1)).findById(rentalId);
        verify(rentalMapper, times(1)).toWithDetailedCarInfoDto(rental);
        verifyNoMoreInteractions(rentalRepository, rentalMapper);
    }

    @Test
    @DisplayName("Verify the exception was thrown when invalid id")
    public void findById_InvalidId_ThrowsException() {
        Long rentalId = -1L;

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> rentalService.findById(rentalId)
        );

        String expected = "Can't find rental with id: " + rentalId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(rentalRepository, times(1)).findById(rentalId);
        verifyNoMoreInteractions(rentalRepository, rentalMapper);
    }

    @Test
    @DisplayName("Verify returnRental() method works")
    public void returnRental_ValidId_ReturnsRentalDto() {
        Long rentalId = 4L;
        Rental rental = RentalSupplier.getRental();
        RentalWithDetailedCarInfoDto expected =
                RentalSupplier.getRentalWithDetailedCarInfoDtoWithId4();

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(rentalMapper.toWithDetailedCarInfoDto(rental)).thenReturn(expected);

        RentalWithDetailedCarInfoDto actual = rentalService.returnRental(rentalId);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(carService, times(1)).returnRentalCar(rental.getCar());
        verify(rentalRepository, times(1)).findById(rentalId);
        verify(rentalRepository, times(1)).save(rental);
        verify(rentalMapper, times(1)).toWithDetailedCarInfoDto(rental);
        verifyNoMoreInteractions(rentalRepository, rentalMapper, carService);
    }

    @Test
    @DisplayName("Verify returnRental() method throws exception when rental does not exists")
    public void returnRental_InvalidId_ThrowsException() {
        Long rentalId = -1L;

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> rentalService.returnRental(rentalId)
        );

        String expected = "Can't find rental with id: " + rentalId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(rentalRepository, times(1)).findById(rentalId);
        verifyNoMoreInteractions(rentalRepository, rentalMapper, carService);
    }

    @Test
    @DisplayName("Verify returnRental() method throws exception when rental is already returned")
    public void returnRental_ReturnedRental_ThrowsException() {
        Long rentalId = 4L;
        Rental rental = RentalSupplier.getRental();
        rental.setActualReturnDate(LocalDate.now());

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));

        RentalIsNotActiveException exception = assertThrows(
                RentalIsNotActiveException.class,
                () -> rentalService.returnRental(rentalId)
        );

        String expected = "This rental is already not active";
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(rentalRepository, times(1)).findById(rentalId);
        verifyNoMoreInteractions(rentalRepository, rentalMapper, carService);
    }

    @Test
    @DisplayName("Verify getAmountToPay() method works by ActualReturnDate")
    public void getAmountToPay_ByActualReturnDate_ReturnsBigDecimal() {
        Rental rental = RentalSupplier.getNotActiveRental();
        RentalWithDetailedCarInfoDto rentalDto =
                RentalSupplier.getRentalWithDetailedCarInfoDtoWithId4();

        when(rentalRepository.findById(rentalDto.id())).thenReturn(Optional.of(rental));
        when(rentalMapper.toWithDetailedCarInfoDto(rental)).thenReturn(rentalDto);

        BigDecimal expected = rentalDto.car().dailyFee().multiply(BigDecimal.valueOf(
                ChronoUnit.DAYS.between(rentalDto.rentalDate(), rentalDto.actualReturnDate())));
        BigDecimal actual = rentalService.getAmountToPay(rentalDto.id());

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(rentalRepository, times(1)).findById(rentalDto.id());
        verify(rentalMapper, times(1)).toWithDetailedCarInfoDto(rental);
        verifyNoMoreInteractions(rentalRepository, rentalMapper);
    }

    @Test
    @DisplayName("Verify getAmountToPay() method works by ReturnDate")
    public void getAmountToPay_ByReturnDate_ReturnsBigDecimal() {
        Rental rental = RentalSupplier.getRental();
        RentalWithDetailedCarInfoDto rentalDto = RentalSupplier.getRentalWithDetailedCarInfoDto();

        when(rentalRepository.findById(rentalDto.id())).thenReturn(Optional.of(rental));
        when(rentalMapper.toWithDetailedCarInfoDto(rental)).thenReturn(rentalDto);

        BigDecimal expected = rentalDto.car().dailyFee().multiply(BigDecimal.valueOf(
                ChronoUnit.DAYS.between(rentalDto.rentalDate(), rentalDto.returnDate())));
        BigDecimal actual = rentalService.getAmountToPay(rentalDto.id());

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(rentalRepository, times(1)).findById(rentalDto.id());
        verify(rentalMapper, times(1)).toWithDetailedCarInfoDto(rental);
        verifyNoMoreInteractions(rentalRepository, rentalMapper);
    }

    @Test
    @DisplayName("Verify getAmountToPay() method works by ReturnDate")
    public void getAmountToPay_OverdueRental_ReturnsBigDecimal() {
        Rental rental = RentalSupplier.getRental();
        RentalWithDetailedCarInfoDto rentalDto =
                RentalSupplier.getOverdueRentalWithDetailedCarInfoDto();

        when(rentalRepository.findById(rentalDto.id())).thenReturn(Optional.of(rental));
        when(rentalMapper.toWithDetailedCarInfoDto(rental)).thenReturn(rentalDto);

        BigDecimal expected = rentalDto.car().dailyFee()
                .multiply(BigDecimal.valueOf(
                        ChronoUnit.DAYS.between(rentalDto.rentalDate(), rentalDto.returnDate())))
                .add(rentalDto.car().dailyFee()
                        .multiply(FINE_MULTIPLIER)
                        .multiply(BigDecimal.valueOf(
                                ChronoUnit.DAYS.between(rentalDto.returnDate(), LocalDate.now()))));
        BigDecimal actual = rentalService.getAmountToPay(rentalDto.id());

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(rentalRepository, times(1)).findById(rentalDto.id());
        verify(rentalMapper, times(1)).toWithDetailedCarInfoDto(rental);
        verifyNoMoreInteractions(rentalRepository, rentalMapper);
    }
}
