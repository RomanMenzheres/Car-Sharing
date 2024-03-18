package com.example.carsharing.service.impl;

import com.example.carsharing.exception.EntityNotFoundException;
import com.example.carsharing.model.TelegramUserInfo;
import com.example.carsharing.model.User;
import com.example.carsharing.repository.TelegramUserInfoRepository;
import com.example.carsharing.security.CustomUserDetailsService;
import com.example.carsharing.service.TelegramUserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramUserInfoServiceImpl implements TelegramUserInfoService {
    private final TelegramUserInfoRepository telegramUserInfoRepository;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public TelegramUserInfo save(String email, String chatId) {
        TelegramUserInfo telegramUserInfo = new TelegramUserInfo();
        telegramUserInfo.setUser((User) userDetailsService.loadUserByUsername(email));
        telegramUserInfo.setChatId(chatId);
        return telegramUserInfoRepository.save(telegramUserInfo);
    }

    @Override
    public TelegramUserInfo findByChatId(String chatId) {
        return telegramUserInfoRepository.findByChatId(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find telegram info "
                        + "about user by chat id: " + chatId));
    }

    @Override
    public TelegramUserInfo findByUserId(Long userId) {
        return telegramUserInfoRepository.findByUserId(userId)
                .orElse(null);
    }
}
