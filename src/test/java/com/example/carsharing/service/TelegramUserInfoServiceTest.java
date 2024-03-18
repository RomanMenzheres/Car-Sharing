package com.example.carsharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.carsharing.exception.EntityNotFoundException;
import com.example.carsharing.model.TelegramUserInfo;
import com.example.carsharing.model.User;
import com.example.carsharing.repository.TelegramUserInfoRepository;
import com.example.carsharing.security.CustomUserDetailsService;
import com.example.carsharing.service.impl.TelegramUserInfoServiceImpl;
import com.example.carsharing.supplier.TelegramUserInfoSupplier;
import com.example.carsharing.supplier.UserSupplier;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TelegramUserInfoServiceTest {
    @Mock
    private TelegramUserInfoRepository telegramUserInfoRepository;
    @Mock
    private CustomUserDetailsService userDetailsService;
    @InjectMocks
    private TelegramUserInfoServiceImpl telegramUserInfoService;

    @Test
    @DisplayName("Verify save() method works")
    public void save_ValidEmail_ReturnsTelegramUserInfo() {
        TelegramUserInfo expected = TelegramUserInfoSupplier.getTelegramUserInfo();
        User user = UserSupplier.getUser();
        String email = user.getEmail();
        String chatId = expected.getChatId();

        when(userDetailsService.loadUserByUsername(email)).thenReturn(user);
        when(telegramUserInfoRepository.save(expected)).thenReturn(expected);

        TelegramUserInfo actual = telegramUserInfoService.save(email, chatId);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(userDetailsService, times(1)).loadUserByUsername(email);
        verify(telegramUserInfoRepository, times(1)).save(expected);
        verifyNoMoreInteractions(telegramUserInfoRepository, userDetailsService);
    }

    @Test
    @DisplayName("Verify findByChatId() method works")
    public void findByChatId_ValidChatId_ReturnsTelegramUserInfo() {
        TelegramUserInfo expected = TelegramUserInfoSupplier.getTelegramUserInfo();
        String chatId = expected.getChatId();

        when(telegramUserInfoRepository.findByChatId(chatId)).thenReturn(Optional.of(expected));

        TelegramUserInfo actual = telegramUserInfoService.findByChatId(chatId);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(telegramUserInfoRepository, times(1)).findByChatId(chatId);
        verifyNoMoreInteractions(telegramUserInfoRepository, userDetailsService);
    }

    @Test
    @DisplayName("Verify findByChatId() method throws exception when chat id is invalid")
    public void findByChatId_InvalidChatId_ThrowsException() {
        String chatId = "NotFoundException";

        when(telegramUserInfoRepository.findByChatId(chatId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> telegramUserInfoService.findByChatId(chatId)
        );

        String expected = "Can't find telegram info about user by chat id: " + chatId;
        String actual = exception.getMessage();

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(telegramUserInfoRepository, times(1)).findByChatId(chatId);
        verifyNoMoreInteractions(telegramUserInfoRepository, userDetailsService);
    }

    @Test
    @DisplayName("Verify findByUserId() method works")
    public void findByUserId_ValidUserId_ReturnsTelegramUserInfo() {
        TelegramUserInfo expected = TelegramUserInfoSupplier.getTelegramUserInfo();
        Long userId = expected.getUser().getId();

        when(telegramUserInfoRepository.findByUserId(userId)).thenReturn(Optional.of(expected));

        TelegramUserInfo actual = telegramUserInfoService.findByUserId(userId);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(telegramUserInfoRepository, times(1)).findByUserId(userId);
        verifyNoMoreInteractions(telegramUserInfoRepository, userDetailsService);
    }

    @Test
    @DisplayName("Verify findByUserId() method throws exception when user id is invalid")
    public void findByUserId_InvalidUserId_ReturnsNull() {
        Long userId = -1L;

        when(telegramUserInfoRepository.findByUserId(userId)).thenReturn(Optional.empty());

        TelegramUserInfo actual = telegramUserInfoService.findByUserId(userId);

        assertNull(actual);
        verify(telegramUserInfoRepository, times(1)).findByUserId(userId);
        verifyNoMoreInteractions(telegramUserInfoRepository, userDetailsService);
    }
}
