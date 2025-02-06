package com.katok.telegramconomy.Bot.Handlers;

import com.katok.telegramconomy.Bot.HandlerExample;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static com.katok.telegramconomy.TelegramConomy.message_cfg;
import static com.katok.telegramconomy.utils.ConfigUtil.getString;

public class help implements HandlerExample {
    @Override
    public String getMessage() {
        return "/help";
    }

    @Override
    public void handler(Update update, TelegramClient client) {
        HandlerExample.sendMessage(update, client, getString("telegram.help", message_cfg));
    }
}
