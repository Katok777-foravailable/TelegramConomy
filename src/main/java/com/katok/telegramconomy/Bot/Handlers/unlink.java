package com.katok.telegramconomy.Bot.Handlers;

import com.katok.telegramconomy.Bot.HandlerExample;
import com.katok.telegramconomy.SQLDatabase;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.katok.telegramconomy.TelegramConomy.database;
import static com.katok.telegramconomy.TelegramConomy.message_cfg;
import static com.katok.telegramconomy.utils.ConfigUtil.getString;

public class unlink implements HandlerExample {
    @Override
    public String getMessage() {
        return "/unlink";
    }

    @Override
    public void handler(Update update, TelegramClient client) {
        PreparedStatement existed_in_another_accounts_statement = database.select(SQLDatabase.getUuid(), SQLDatabase.getTelegram_id(), String.valueOf(update.getMessage().getChatId()));
        ResultSet existed_in_another_accounts;

        try {
            existed_in_another_accounts = existed_in_another_accounts_statement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String confirm_result = getString("telegram.unlink.notSuccessful", message_cfg);

        try {
            if(existed_in_another_accounts.next()) {
                database.drop_by_telegram_id(String.valueOf(update.getMessage().getChatId()));
                confirm_result = getString("telegram.unlink.successful", message_cfg);
            }

            existed_in_another_accounts.close();
            existed_in_another_accounts_statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        HandlerExample.sendMessage(update, client, confirm_result);
    }
}
