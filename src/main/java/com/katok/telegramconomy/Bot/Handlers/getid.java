package com.katok.telegramconomy.Bot.Handlers;

import com.katok.telegramconomy.Bot.HandlerExample;
import org.apache.commons.lang.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.text.MessageFormat;

import static com.katok.telegramconomy.TelegramConomy.message_cfg;
import static com.katok.telegramconomy.utils.ConfigUtil.getString;

public class getid implements HandlerExample {
    @Override
    public String getMessage() {
        return "/getid";
    }

    @Override
    public void handler(Update update, TelegramClient client) {
        SendMessage sendMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()), MessageFormat.format(getString("telegram.yourId", message_cfg), String.valueOf(update.getMessage().getChatId())));

        try {
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
