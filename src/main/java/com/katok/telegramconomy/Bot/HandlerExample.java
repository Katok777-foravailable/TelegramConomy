package com.katok.telegramconomy.Bot;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public interface HandlerExample {
    String getMessage();
    void handler(Update update, TelegramClient client);

    static void sendMessage(Update update, TelegramClient client, String text) {
        SendMessage sendMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()), text);
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        try {
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
