package com.example.carsharing.repository;

import com.example.carsharing.model.TelegramUserInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramUserInfoRepository extends JpaRepository<TelegramUserInfo, Long> {
    Optional<TelegramUserInfo> findByChatId(String chatId);

    Optional<TelegramUserInfo> findByUserId(Long userId);
}
