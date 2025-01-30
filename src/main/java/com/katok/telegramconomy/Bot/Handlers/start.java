package com.katok.telegramconomy.Bot.Handlers;

import com.katok.telegramconomy.Bot.HandlerExample;
import org.telegram.telegrambots.meta.api.objects.Update;

public class start implements HandlerExample {
    @Override
    public String getMessage() {
        return "/start";
    }

    @Override
    public void handler(Update update) {

    }
}
