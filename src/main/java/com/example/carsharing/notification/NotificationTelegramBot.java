package com.example.carsharing.notification;

import com.example.carsharing.exception.TelegramBotMessageException;
import com.example.carsharing.repository.TelegramUserInfoRepository;
import com.example.carsharing.service.TelegramUserInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class NotificationTelegramBot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botName;
    private final TelegramUserInfoService telegramUserInfoService;
    private final TelegramUserInfoRepository telegramUserInfoRepository;

    public NotificationTelegramBot(@Value("${bot.key}") String botKey,
                                   TelegramUserInfoService telegramUserInfoService,
                                   TelegramUserInfoRepository telegramUserInfoRepository) {
        super(botKey);
        this.telegramUserInfoService = telegramUserInfoService;
        this.telegramUserInfoRepository = telegramUserInfoRepository;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        System.out.println(telegramUserInfoRepository.findByChatId(chatId));
        if (telegramUserInfoRepository.findByChatId(chatId).isEmpty()
                && update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            if (message.equals("/start")) {
                startingMessage(chatId);
            } else {
                validateUser(message, chatId);
            }
        }
    }

    private void startingMessage(String chatId) {
        String message = """
                This bot will send you notifications on:
                - Rentals creation
                - Payments creation
                - Successful payments
                - Overdue rentals

                Please, enter your email to authorize""";
        sendMessage(message, chatId);
    }

    private void validateUser(String email, String chatId) {
        try {
            telegramUserInfoService.save(email, chatId);
            sendMessage("Authorized successfully!", chatId);
        } catch (Exception e) {
            sendMessage("Email is invalid! Please, try again.", chatId);
        }
    }

    public void sendMessage(String value, String chatId) {
        if (chatId == null) {
            return;
        }

        SendMessage message = new SendMessage(chatId, value);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new TelegramBotMessageException("Couldn't send a message: " + value, e);
        }
    }
}
