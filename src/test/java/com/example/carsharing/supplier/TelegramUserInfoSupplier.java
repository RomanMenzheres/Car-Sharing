package com.example.carsharing.supplier;

import com.example.carsharing.model.TelegramUserInfo;

public class TelegramUserInfoSupplier {

    public static TelegramUserInfo getTelegramUserInfo() {
        TelegramUserInfo userInfo = new TelegramUserInfo();
        userInfo.setUser(UserSupplier.getUser());
        userInfo.setChatId("666666");
        return userInfo;
    }
}
