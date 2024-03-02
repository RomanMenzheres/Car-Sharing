package com.example.carsharing.notification;

import com.example.carsharing.exception.TelegramBotMessageException;
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
    @Value("${bot.chatId}")
    private String chatId;

    public NotificationTelegramBot(@Value("${bot.key}") String botKey) {
        super(botKey);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        chatId = String.valueOf(update.getMessage().getChatId());
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            if (message.equals("/start")) {
                startingMessage();
            }
        }
    }

    private void startingMessage() {
        String message =
                "This bot will send you notifications on:"
                + "\n- Created rentals"
                + "\n- Successful payments"
                + "\n-Overdue rentals";
        sendMessage(message);
    }

    public void sendMessage(String value) {
        SendMessage message = new SendMessage(chatId, value);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new TelegramBotMessageException("Couldn't send a message: " + value, e);
        }
    }
}
