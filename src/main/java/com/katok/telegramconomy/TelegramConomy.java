package com.katok.telegramconomy;

import com.katok.telegramconomy.Bot.bot;
import com.katok.telegramconomy.Commands.telegramconomy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

import static com.katok.telegramconomy.Bot.bot.unload;
import static com.katok.telegramconomy.utils.ConfigUtil.getString;

public final class TelegramConomy extends JavaPlugin {
    public static TelegramConomy instance;
    public static Economy economy;
    public static Logger logger;
    public static FileConfiguration config;

    public static YamlConfiguration message_cfg;
    public static File message_file;

    public static YamlConfiguration telegram_cfg;
    public static File telegram_file;

    public static SQLDatabase database;

    public static String token;
    public static String bot_name;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();

        load_configs();

        // SQL
        database = new SQLDatabase(instance.getDataFolder());

        // economy
        RegisteredServiceProvider<Economy> vault_class = instance.getServer().getServicesManager().getRegistration(Economy.class);
        if (vault_class == null) {
            logger.severe("Не удалось загрузить экономику!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        economy = vault_class.getProvider();

        // telegram
        token = getString("token");
        bot_name = getString("username");
        if(!bot.load(token)) getServer().getPluginManager().disablePlugin(this);

        // команды
        getCommand("telegramconomy").setExecutor(new telegramconomy());
    }

    @Override
    public void onDisable() {
        unload(token);
    }

    public void load_configs() {
        saveDefaultConfig();
        config = getConfig();

        message_cfg = new YamlConfiguration();
        message_file = new File(instance.getDataFolder(), "messages.yml");

        if(!message_file.exists()) instance.saveResource("messages.yml", false);

        try {
            message_cfg.load(message_file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        telegram_cfg = new YamlConfiguration();
        telegram_file = new File(instance.getDataFolder(), "telegram.yml");

        if(!telegram_file.exists()) instance.saveResource("telegram.yml", false);

        try {
            telegram_cfg.load(telegram_file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
