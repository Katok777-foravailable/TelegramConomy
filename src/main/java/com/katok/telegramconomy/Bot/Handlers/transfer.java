package com.katok.telegramconomy.Bot.Handlers;

import com.katok.telegramconomy.Bot.HandlerExample;
import com.katok.telegramconomy.SQLDatabase;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static com.katok.telegramconomy.TelegramConomy.*;
import static com.katok.telegramconomy.utils.ConfigUtil.getString;

public class transfer implements HandlerExample {
    @Override
    public String getMessage() {
        return "/transfer";
    }

    @Override
    public void handler(Update update, TelegramClient client) {
        String[] arguments = update.getMessage().getText().split(" ");

        if(arguments.length < 3) {
            HandlerExample.sendMessage(update, client, getString("telegram.examples.notEnoughArguments", message_cfg) + getString("telegram.transfer.example", message_cfg));
            return;
        }
        if(arguments[2].length() > 6 || !StringUtils.isNumeric(arguments[2])) {
            HandlerExample.sendMessage(update, client, getString("telegram.examples.notValidMoney", message_cfg) + getString("telegram.transfer.example", message_cfg));
            return;
        }
        if(!StringUtils.isNumeric(arguments[1])) {
            HandlerExample.sendMessage(update, client, getString("telegram.examples.youMustWriteID", message_cfg) + getString("telegram.transfer.example", message_cfg));
            return;
        }

        int cash = Integer.parseInt(arguments[2]);

        OfflinePlayer sender;
        OfflinePlayer getter;

        try {
            PreparedStatement preparedStatement_sender = database.select(SQLDatabase.getUuid(), SQLDatabase.getTelegram_id(), String.valueOf(update.getMessage().getChatId()));
            ResultSet resultSet_sender = preparedStatement_sender.executeQuery();

            PreparedStatement preparedStatement_getter = database.select(SQLDatabase.getUuid(), SQLDatabase.getTelegram_id(), arguments[1]);
            ResultSet resultSet_getter = preparedStatement_getter.executeQuery();

            if(!resultSet_sender.next()) {
                HandlerExample.sendMessage(update, client, getString("telegram.youDontHaveAccount", message_cfg));
                resultSet_sender.close();
                preparedStatement_sender.close();
                return;
            }

            if(!resultSet_getter.next()) {
                HandlerExample.sendMessage(update, client, getString("telegram.examples.thisIDnotExist", message_cfg) + getString("telegram.transfer.example", message_cfg));
                resultSet_getter.close();
                preparedStatement_getter.close();
                return;
            }

            sender = Bukkit.getOfflinePlayer(UUID.fromString(resultSet_sender.getString(SQLDatabase.getUuid())));
            getter = Bukkit.getOfflinePlayer(UUID.fromString(resultSet_getter.getString(SQLDatabase.getUuid())));

            resultSet_sender.close();
            preparedStatement_sender.close();
            resultSet_getter.close();
            preparedStatement_getter.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if(!economy.hasAccount(getter)) {
            HandlerExample.sendMessage(update, client, getString("telegram.examples.thisIDnotExist", message_cfg) + getString("telegram.transfer.example", message_cfg));
            return;
        }
        if(!economy.hasAccount(sender) || !economy.has(sender, cash)) {
            HandlerExample.sendMessage(update, client, getString("telegram.notEnoughMoney", message_cfg));
            return;
        }

        economy.withdrawPlayer(sender, cash);
        economy.depositPlayer(getter, cash);

        HandlerExample.sendMessage(update, client, getString("telegram.transfer.successful", message_cfg));
    }
}
