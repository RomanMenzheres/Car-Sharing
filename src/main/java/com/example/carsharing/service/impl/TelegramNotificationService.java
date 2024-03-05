package com.example.carsharing.service.impl;

import com.example.carsharing.dto.payment.PaymentDto;
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
                + "\nRental Date: " + rental.rentalDate()
                + "\nReturn Date: " + rental.returnDate();
        bot.sendMessage(message);
    }

    @Override
    public void scheduledOverdueRentalNotification(List<RentalDto> overdueRentals) {
        StringBuilder message = new StringBuilder();

        if (overdueRentals.isEmpty()) {
            message.append("--- NO OVERDUE RENTALS TODAY ---");
        } else {
            message.append("--- NEAREST OVERDUE RENTALS ---\n");
            for (RentalDto rental : overdueRentals) {
                message.append("\nID: ").append(rental.id());
                message.append("\nUser ID: ").append(rental.userId());
                message.append("\nCar ID: ").append(rental.carId());
                message.append("\nRental Date: ").append(rental.rentalDate());
                message.append("\nReturn Date: ").append(rental.returnDate()).append("\n");
            }
        }

        message.append("\nYou will be FINED for overdue rentals. "
                + "You will be charged an additional fee for each penalty day!");
        bot.sendMessage(message.toString());
    }

    @Override
    public void onPaymentCreationNotification(PaymentDto paymentDto) {
        String message = "--- NEW PAYMENT ---\n"
                + "\nID: " + paymentDto.id()
                + "\nFor: rental with id: " + paymentDto.rentalId()
                + "\nStatus: " + paymentDto.status()
                + "\nType: " + paymentDto.type()
                + "\nLink to pay: " + paymentDto.sessionUrl()
                + "\nAmount To Pay: " + paymentDto.amountToPay();
        bot.sendMessage(message);
    }

    @Override
    public void onSuccessfulPayment(PaymentDto paymentDto) {
        String message = "Payment with id: " + paymentDto.id() + " paid successfully!"
                + "\n\nThank you for choosing us!";
        bot.sendMessage(message);
    }
}
