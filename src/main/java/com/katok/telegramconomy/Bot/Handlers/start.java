package com.katok.telegramconomy.Bot.Handlers;

import com.katok.telegramconomy.Bot.HandlerExample;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class start implements HandlerExample {
    @Override
    public String getMessage() {
        return "/start";
    }

    @Override
    public void handler(Update update, TelegramClient client) {
        SendMessage sendMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()), String.valueOf(update.getMessage().getChatId()));

        try {
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
