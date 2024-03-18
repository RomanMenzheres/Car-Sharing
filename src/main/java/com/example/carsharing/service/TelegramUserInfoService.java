package com.example.carsharing.service;

import com.example.carsharing.model.TelegramUserInfo;

public interface TelegramUserInfoService {
    TelegramUserInfo save(String email, String chatId);

    TelegramUserInfo findByChatId(String chatId);

    TelegramUserInfo findByUserId(Long userId);
}
