package com.katok.telegramconomy.Bot;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface HandlerExample {
    public String getMessage();
    public void handler(Update update);
}
