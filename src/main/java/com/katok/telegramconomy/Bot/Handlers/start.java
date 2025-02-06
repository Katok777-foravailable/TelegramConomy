package com.katok.telegramconomy.Bot.Handlers;

import com.katok.telegramconomy.Bot.HandlerExample;
import com.katok.telegramconomy.SQLDatabase;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.UUID;

import static com.katok.telegramconomy.TelegramConomy.*;
import static com.katok.telegramconomy.utils.ConfigUtil.getString;

public class start implements HandlerExample {
    @Override
    public String getMessage() {
        return "/start";
    }

    @Override
    public void handler(Update update, TelegramClient client) {
        if(check_check(update, client)) return;

        PreparedStatement statement = database.select(SQLDatabase.getUuid(), SQLDatabase.getTelegram_id(), String.valueOf(update.getMessage().getChatId()));
        ResultSet resultSet;

        try {
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String name_player;
        String balance;
        OfflinePlayer player;

        try {
            if (resultSet.isBeforeFirst()) {
                player = Bukkit.getOfflinePlayer(UUID.fromString(resultSet.getString(SQLDatabase.getUuid())));
                name_player = player.getName();
                balance = String.valueOf(economy.getBalance(player));
            } else {
                name_player = getString("telegram.absence", message_cfg);
                balance = getString("telegram.absence", message_cfg);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        SendMessage sendMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()),
                MessageFormat.format(getString("telegram.startmessage", message_cfg), name_player, balance));

        try {
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private Boolean check_check(Update update, TelegramClient client) {
        String[] arguments = update.getMessage().getText().split(" ");

        if(arguments.length < 2 || arguments[1].length() != 6 || !StringUtils.isNumeric(arguments[1])) return false;

        OfflinePlayer player;

        try {
            PreparedStatement preparedStatement = database.select(SQLDatabase.getUuid(), SQLDatabase.getTelegram_id(), String.valueOf(update.getMessage().getChatId()));
            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next()) {
                resultSet.close();
                preparedStatement.close();
                return false;
            }

            player = Bukkit.getOfflinePlayer(UUID.fromString(resultSet.getString(SQLDatabase.getUuid())));

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if(!(economy.hasAccount(player) && telegram_cfg.contains(arguments[1]))) return false;

        economy.depositPlayer(player, telegram_cfg.getInt(arguments[1]));
        int cash = telegram_cfg.getInt(arguments[1]);
        telegram_cfg.set(arguments[1], null);

        try {
            telegram_cfg.save(telegram_file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HandlerExample.sendMessage(update, client, MessageFormat.format(getString("telegram.check.youClaimCheck", message_cfg), economy.format(cash)));
        return true;
    }
}
