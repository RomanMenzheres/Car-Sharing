package com.example.carsharing.service.impl;

import com.example.carsharing.dto.rental.RentalDto;
import com.example.carsharing.dto.rental.RentalWithDetailedCarInfoDto;
import com.example.carsharing.notification.NotificationTelegramBot;
import com.example.carsharing.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final NotificationTelegramBot bot;

    @Override
    public void onRentalCreationNotification(RentalWithDetailedCarInfoDto rental) {
        String message = "--- NEW RENTAL ---\n"
                + "\nID: " + rental.id()
                + "\nUser ID: " + rental.userId()
                + "\nCar Brand: " + rental.car().brand()
                + "\nCar Model: " + rental.car().model()
                + "\nCar Type: " + rental.car().type()
                + "\nCar Daily Fee: " + rental.car().dailyFee()
                + "\nRental Date: " + rental.rentalDateTime()
                + "\nReturn Date: " + rental.returnDateTime();
        bot.sendMessage(message);
    }

    @Override
    public void scheduledOverdueRentalNotification(List<RentalDto> overdueRentals) {
        StringBuilder message = new StringBuilder();

        if (overdueRentals.isEmpty()) {
            message.append("--- NO OVERDUE RENTALS TODAY ---");
        } else {
            message.append("--- OVERDUE RENTALS ---\n");
            for (RentalDto rental : overdueRentals) {
                message.append("\nID: ").append(rental.id());
                message.append("\nUser ID: ").append(rental.userId());
                message.append("\nCar ID: ").append(rental.carId());
                message.append("\nRental Date: ").append(rental.rentalDateTime());
                message.append("\nReturn Date: ").append(rental.returnDateTime()).append("\n");
            }
        }

        bot.sendMessage(message.toString());
    }
}
