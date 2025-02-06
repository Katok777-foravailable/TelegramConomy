package com.katok.telegramconomy.Bot.Handlers;

import com.katok.telegramconomy.Bot.HandlerExample;
import com.katok.telegramconomy.SQLDatabase;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.UUID;

import static com.katok.telegramconomy.TelegramConomy.*;
import static com.katok.telegramconomy.utils.ConfigUtil.getString;

public class check implements HandlerExample {
    @Override
    public String getMessage() {
        return "/check";
    }

    @Override
    public void handler(Update update, TelegramClient client) {
        String[] strings = update.getMessage().getText().split(" ");
        if(strings.length < 2 || !StringUtils.isNumeric(strings[1]) || strings[1].length() > 6) {
            HandlerExample.sendMessage(update, client, getString("telegram.check.notEnoughArguments", message_cfg));
            return;
        }
        int cash = Integer.parseInt(strings[1]);
        OfflinePlayer player;

        try {
            PreparedStatement preparedStatement = database.select(SQLDatabase.getUuid(), SQLDatabase.getTelegram_id(), String.valueOf(update.getMessage().getChatId()));
            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next()) {
                HandlerExample.sendMessage(update, client, getString("telegram.youDontHaveAccount", message_cfg));
                resultSet.close();
                preparedStatement.close();
                return;
            }

            player = Bukkit.getOfflinePlayer(UUID.fromString(resultSet.getString(SQLDatabase.getUuid())));

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if(economy.getBalance(player) < cash) {
            HandlerExample.sendMessage(update, client, getString("telegram.check.notEnoughMoney", message_cfg));
            return;
        }

        economy.withdrawPlayer(player, cash);

        String check_index = String.valueOf(Math.random() * 1000000000).substring(0, 7).replaceAll("\\.", ""); // почему 7 а не 6? потому-что точка чудным образом преобразуется в пустоту, и выходит недостаточно чисел, так что 7 превращается в 6

        telegram_cfg.set(check_index, cash);
        try {
            telegram_cfg.save(telegram_file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HandlerExample.sendMessage(update, client, MessageFormat.format(getString("telegram.check.successful", message_cfg), "t.me/" + bot_name + "?start=" + check_index));
    }
}
