package com.example.carsharing.supplier;

import com.example.carsharing.dto.rental.CreateRentalRequestDto;
import com.example.carsharing.dto.rental.RentalDto;
import com.example.carsharing.dto.rental.RentalWithDetailedCarInfoDto;
import com.example.carsharing.model.Rental;
import java.time.LocalDate;
import java.util.List;

public class RentalSupplier {
    public static CreateRentalRequestDto getCreateRentalRequestDto() {
        return new CreateRentalRequestDto(
                4L,
                LocalDate.of(2025, 3, 15)
        );
    }

    public static RentalWithDetailedCarInfoDto getRentalWithDetailedCarInfoDto() {
        return new RentalWithDetailedCarInfoDto(
                1L,
                1L,
                CarSupplier.getCarWithId2(),
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                null
        );
    }

    public static RentalWithDetailedCarInfoDto getRentalWithDetailedCarInfoDtoWithId4() {
        return new RentalWithDetailedCarInfoDto(
                4L,
                1L,
                CarSupplier.getCarDto(),
                LocalDate.now(),
                LocalDate.of(2025, 3, 15),
                LocalDate.now().plusDays(2)
        );
    }

    public static CreateRentalRequestDto getInvalidCreateRentalRequestDto() {
        return new CreateRentalRequestDto(
                -1L,
                LocalDate.of(2024, 2, 1)
        );
    }

    public static List<RentalDto> getAllRentalsByUserWithId1() {
        return List.of(
                getRentalDtoWithId2(),

                new RentalDto(
                        3L,
                        1L,
                        3L,
                        LocalDate.of(2024, 3, 7),
                        LocalDate.of(2025, 3, 13),
                        LocalDate.of(2024, 3, 10)
                )
        );
    }

    public static RentalWithDetailedCarInfoDto getCarWithId2() {
        return new RentalWithDetailedCarInfoDto(
                2L,
                4L,
                CarSupplier.getCarWithId2(),
                LocalDate.of(2024, 3, 9),
                LocalDate.of(2024, 3, 15),
                null
        );
    }

    public static List<RentalDto> getAllActiveRentalsByUserWithId1() {
        return List.of(getRentalDtoWithId2());
    }

    public static List<RentalDto> getAllActiveRentals() {
        return List.of(
                getRentalDtoWithId2(),

                new RentalDto(
                        4L,
                        2L,
                        4L,
                        LocalDate.of(2024, 3, 8),
                        LocalDate.of(2025, 3, 14),
                        null
                )
        );
    }

    private static RentalDto getRentalDtoWithId2() {
        return new RentalDto(
                2L,
                1L,
                2L,
                LocalDate.of(2024, 3, 9),
                LocalDate.of(2025, 3, 15),
                null
        );
    }

    public static Rental getRental() {
        Rental rental = new Rental();
        rental.setCar(CarSupplier.getCar());
        rental.setUser(UserSupplier.getUser());
        rental.setRentalDate(LocalDate.of(2024, 3, 10));
        rental.setReturnDate(LocalDate.of(2025, 3, 15));
        return rental;
    }

    public static Rental getNotActiveRental() {
        Rental rental = new Rental();
        rental.setId(4L);
        rental.setCar(CarSupplier.getCar());
        rental.setUser(UserSupplier.getUser());
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(LocalDate.of(2025, 3, 15));
        rental.setActualReturnDate(LocalDate.now().plusDays(2));
        return rental;
    }

    public static RentalDto getRentalDto() {
        return new RentalDto(
                4L,
                1L,
                4L,
                LocalDate.of(2024, 3, 10),
                LocalDate.of(2025, 3, 15),
                null
        );
    }

    public static RentalWithDetailedCarInfoDto getOverdueRentalWithDetailedCarInfoDto() {
        return new RentalWithDetailedCarInfoDto(
                1L,
                1L,
                CarSupplier.getCarWithId2(),
                LocalDate.of(2024, 3, 9),
                LocalDate.of(2024, 3, 11),
                null
        );
    }
}
