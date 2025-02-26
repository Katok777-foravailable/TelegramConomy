package com.katok.telegramconomy.Commands;

import com.katok.telegramconomy.SQLDatabase;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

import static com.katok.telegramconomy.TelegramConomy.*;
import static com.katok.telegramconomy.utils.ConfigUtil.getString;

public class telegramconomy implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(getString("minecraft.other.cantWriteFromConsole", message_cfg));
            return true;
        }
        if(strings.length == 0) {
            final String[] linked = {getString("minecraft.link.notlinked", message_cfg)};
            Bukkit.getScheduler().runTaskAsynchronously(instance, new Runnable() {
                @Override
                public void run() {
                    PreparedStatement statement = database.select(SQLDatabase.getTelegram_id(), SQLDatabase.getUuid(), player.getUniqueId().toString());
                    ResultSet resultSet;

                    try {
                        resultSet = statement.executeQuery();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        if(resultSet.next()) {
                            linked[0] = getString("minecraft.link.linked", message_cfg);
                        }
                        resultSet.close();
                        statement.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    player.sendMessage(MessageFormat.format(getString("minecraft.link.Doyoulinked", message_cfg), linked[0]));
                }
            });
            return true;
        }
        switch (strings[0]) {
            case("confirm"):
                if(strings.length < 2) {
                    player.sendMessage(getString("minecraft.other.notEnoughArguments", message_cfg));
                    break;
                }
                if(strings[1].length() > 25 || !StringUtils.isNumeric(strings[1])) {
                    player.sendMessage(getString("minecraft.confirm.youMustWriteID", message_cfg));
                    break;
                }

                Bukkit.getScheduler().runTaskAsynchronously(instance, new Runnable() {
                    @Override
                    public void run() {
                        PreparedStatement existed_in_another_accounts_statement = database.select(SQLDatabase.getTelegram_id(), SQLDatabase.getUuid(), player.getUniqueId().toString());
                        PreparedStatement busy_id_statement = database.select(SQLDatabase.getTelegram_id(), SQLDatabase.getTelegram_id(), strings[1]);

                        ResultSet existed_in_another_accounts;
                        ResultSet busy_id;

                        try {
                            existed_in_another_accounts = existed_in_another_accounts_statement.executeQuery();
                            busy_id = busy_id_statement.executeQuery();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        String confirm_result = getString("minecraft.confirm.thisIDAlreadyTaken", message_cfg);
                        boolean can = true;

                        try {
                            if(busy_id.next()) {
                                can = false;
                            } else if(existed_in_another_accounts.next()) {
                                database.drop_by_uuid(player.getUniqueId().toString());
                            }
                            existed_in_another_accounts.close();
                            busy_id.close();

                            existed_in_another_accounts_statement.close();
                            busy_id_statement.close();

                            if(can) {
                                database.insert(player.getUniqueId().toString(), strings[1]);
                                confirm_result = getString("minecraft.confirm.youSuccessfulLinked", message_cfg);
                            }

                        } catch (SQLException e) { 
                            throw new RuntimeException(e);
                        }
                        player.sendMessage(confirm_result);
                    }
                });
                break;
            case("unlink"):
                Bukkit.getScheduler().runTaskAsynchronously(instance, new Runnable() {
                    @Override
                    public void run() {
                        PreparedStatement existed_in_another_accounts_statement = database.select(SQLDatabase.getTelegram_id(), SQLDatabase.getUuid(), player.getUniqueId().toString());
                        ResultSet existed_in_another_accounts;

                        try {
                            existed_in_another_accounts = existed_in_another_accounts_statement.executeQuery();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        String confirm_result = getString("minecraft.unlink.notSuccessful", message_cfg);

                        try {
                            if(existed_in_another_accounts.next()) {
                                database.drop_by_uuid(player.getUniqueId().toString());
                                confirm_result = getString("minecraft.unlink.successful", message_cfg);
                            }

                            existed_in_another_accounts.close();
                            existed_in_another_accounts_statement.close();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        player.sendMessage(confirm_result);
                    }
                });
                break;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(strings.length <= 1) {
            return List.of("confirm", "unlink");
        }
        return List.of();
    }
}
