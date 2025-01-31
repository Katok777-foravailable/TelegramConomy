package com.katok.telegramconomy.Bot;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public interface HandlerExample {
    public String getMessage();
    public void handler(Update update, TelegramClient client);
}
