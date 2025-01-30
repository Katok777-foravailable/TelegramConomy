package com.katok.telegramconomy.Bot;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.reflections.Reflections;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;

import static com.katok.telegramconomy.TelegramConomy.*;

public class bot implements LongPollingSingleThreadUpdateConsumer {
    public final String token;
    public final int cooldown;

    public HashMap<Long, Integer> mute_users = new HashMap<>();
    public HashMap<String, Consumer<Update>> handlers = new HashMap<>();

    public bot(String token, String path) {
        this.token = token;
        this.cooldown = config.getInt("antispam_cooldown");

        Reflections reflections = new Reflections(path);

        Set<Class<? extends HandlerExample>> allClasses = reflections.getSubTypesOf(HandlerExample.class);

        for(Class<? extends HandlerExample> handlerClass: allClasses) {
            try {
                HandlerExample handler = handlerClass.newInstance();
                handlers.put(handler.getMessage().toLowerCase(), handler::handler);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, new Runnable() {
            @Override
            public void run() {
                for(Long id: mute_users.keySet()) {
                    if(mute_users.get(id) <= 0) {
                        mute_users.remove(id);
                    } else {
                        mute_users.replace(id, mute_users.get(id) - 1);
                    }
                }
            }
        }, 20, 20);
    }

    @Override
    public void consume(Update update) {
        if(!(update.hasMessage() && update.getMessage().hasText())) return;

        if(mute_users.containsKey(update.getMessage().getChatId())) {
            return;
        }
        mute_users.put(update.getMessage().getChatId(), cooldown);

        if(!telegram_cfg.contains("accounts." + update.getMessage().getChatId())) {
            telegram_cfg.set("accounts." + update.getMessage().getChatId(), "");

            try {
                telegram_cfg.save(telegram_file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for(String message: handlers.keySet()) {
            if(message.equalsIgnoreCase(update.getMessage().getText())) {
                handlers.get(message).accept(update);
                break;
            }
        }
    }

    public static Boolean load(String token) {
        if(StringUtils.isEmpty(token)) {
            logger.severe("Пожалуйста, заполните данные о боте в конфиге");
            return false;
        }

        TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
        try {
            botsApplication.registerBot(token, new bot(token, "com.katok.telegramconomy.Bot.Handlers"));
        } catch (TelegramApiException e) {
            logger.severe("Не удалось загрузить телеграм бота, проверьте правильность токена либо подключение к интернету");
            return false;
        }
        return true;
    }
}
