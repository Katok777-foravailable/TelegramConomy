package com.katok.telegramconomy.Bot;

import com.katok.telegramconomy.Bot.Handlers.start;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.katok.telegramconomy.TelegramConomy.*;

public class bot implements LongPollingSingleThreadUpdateConsumer {
    public final String token;
    public final int cooldown;

    public List<Long> mute_users = new ArrayList<>();
    public HashMap<String, BiConsumer<Update, TelegramClient>> handlers = new HashMap<>();
    private TelegramClient client;

    public bot(String token) {
        this.token = token;
        this.cooldown = config.getInt("antispam_cooldown");
        this.client = new OkHttpTelegramClient(token);

        load_module(start.class);
    }

    @Override
    public void consume(Update update) {
        if(!(update.hasMessage() && update.getMessage().hasText())) return;

        if(mute_users.contains(update.getMessage().getChatId())) {
            return;
        }
        mute_users.add(update.getMessage().getChatId());
        Bukkit.getScheduler().runTaskLaterAsynchronously(instance, new Runnable() {
            @Override
            public void run() {
                mute_users.remove(update.getMessage().getChatId());
            }
        }, 20L * cooldown);

        if(!telegram_cfg.contains("accounts." + update.getMessage().getChatId())) {
            telegram_cfg.set("accounts." + update.getMessage().getChatId(), "");

            try {
                telegram_cfg.save(telegram_file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for(String message: handlers.keySet()) {
            if(update.getMessage().getText().startsWith(message)) {
                handlers.get(message).accept(update, client);
                break;
            }
        }
    }

    public void load_module(Class<? extends HandlerExample> handlerClass) {
        try {
            HandlerExample handler = handlerClass.newInstance();
            handlers.put(handler.getMessage(), handler::handler);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Boolean load(String token) {
        if(StringUtils.isEmpty(token)) {
            logger.severe("Пожалуйста, заполните данные о боте в конфиге");
            return false;
        }

        TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
        try {
            botsApplication.registerBot(token, new bot(token));
        } catch (TelegramApiException e) {
            logger.severe("Не удалось загрузить телеграм бота, проверьте правильность токена либо подключение к интернету");
            return false;
        }
        return true;
    }
    public static void unload(String token) {
        TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
        try {
            botsApplication.unregisterBot(token);
        } catch (TelegramApiException e) {}
    }
}
