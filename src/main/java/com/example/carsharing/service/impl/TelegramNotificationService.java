package com.example.carsharing.service.impl;

import com.example.carsharing.dto.payment.PaymentDto;
import com.example.carsharing.dto.rental.RentalDto;
import com.example.carsharing.dto.rental.RentalWithDetailedCarInfoDto;
import com.example.carsharing.model.TelegramUserInfo;
import com.example.carsharing.notification.NotificationTelegramBot;
import com.example.carsharing.service.NotificationService;
import com.example.carsharing.service.TelegramUserInfoService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final NotificationTelegramBot bot;
    private final TelegramUserInfoService telegramUserInfoService;

    @Override
    public void onRentalCreationNotification(RentalWithDetailedCarInfoDto rental) {
        Long userId = rental.userId();

        String message = "--- NEW RENTAL ---\n"
                + "\nID: " + rental.id()
                + "\nUser ID: " + userId
                + "\nCar Brand: " + rental.car().brand()
                + "\nCar Model: " + rental.car().model()
                + "\nCar Type: " + rental.car().type()
                + "\nCar Daily Fee: " + rental.car().dailyFee()
                + "\nRental Date: " + rental.rentalDate()
                + "\nReturn Date: " + rental.returnDate();
        bot.sendMessage(message, getChatIdByUser(userId));
    }

    @Override
    public void scheduledOverdueRentalNotification(Map<Long, List<RentalDto>> overdueRentals) {
        for (Map.Entry<Long, List<RentalDto>> overdueRental : overdueRentals.entrySet()) {
            StringBuilder message = new StringBuilder();

            if (overdueRental.getValue().isEmpty()) {
                message.append("--- NO OVERDUE RENTALS TODAY ---");
            } else {
                message.append("--- NEAREST OVERDUE RENTALS ---\n");
                for (RentalDto rental : overdueRental.getValue()) {
                    message.append("\nID: ").append(rental.id());
                    message.append("\nUser ID: ").append(rental.userId());
                    message.append("\nCar ID: ").append(rental.carId());
                    message.append("\nRental Date: ").append(rental.rentalDate());
                    message.append("\nReturn Date: ").append(rental.returnDate()).append("\n");
                }
            }

            message.append("\nYou will be FINED for overdue rentals. "
                    + "You will be charged an additional fee for each penalty day!");

            Long userId = overdueRental.getKey();
            bot.sendMessage(message.toString(), getChatIdByUser(userId));
        }
    }

    @Override
    public void onPaymentCreationNotification(PaymentDto paymentDto, Long userId) {
        String message = "--- NEW PAYMENT ---\n"
                + "\nID: " + paymentDto.id()
                + "\nFor: rental with id: " + paymentDto.rentalId()
                + "\nStatus: " + paymentDto.status()
                + "\nType: " + paymentDto.type()
                + "\nLink to pay: " + paymentDto.sessionUrl()
                + "\nAmount To Pay: " + paymentDto.amountToPay();
        bot.sendMessage(message, getChatIdByUser(userId));
    }

    @Override
    public void onSuccessfulPayment(PaymentDto paymentDto, Long userId) {
        String message = "Payment with id: " + paymentDto.id() + " paid successfully!"
                + "\n\nThank you for choosing us!";
        bot.sendMessage(message, getChatIdByUser(userId));
    }

    private String getChatIdByUser(Long userId) {
        TelegramUserInfo telegramUserInfo = telegramUserInfoService.findByUserId(userId);
        return telegramUserInfo == null ? null : telegramUserInfo.getChatId();
    }
}
