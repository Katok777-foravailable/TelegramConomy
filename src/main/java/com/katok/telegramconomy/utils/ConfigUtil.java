package com.katok.telegramconomy.utils;


import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;

import static com.katok.telegramconomy.TelegramConomy.instance;

public class ConfigUtil {
    public static String getString(String path) {
        return HexUtil.color(instance.getConfig().getString(path));
    }
    public static Component getString_component(String path) {
        return HexUtil.Component_HEX(instance.getConfig().getString(path));
    }

    public static String getString(String path, FileConfiguration config) {
        return HexUtil.color(config.getString(path));
    }
    public static Component getString_component(String path, FileConfiguration config) {
        return HexUtil.Component_HEX(config.getString(path));
    }

    public static int getInt(String path) {
        return instance.getConfig().getInt(path);
    }
    public static int getInt(String path, FileConfiguration config) {
        return config.getInt(path);
    }

    public static Boolean getBoolean(String path){
        return instance.getConfig().getBoolean(path);
    }
    public static Boolean getBoolean(String path, FileConfiguration config){
        return config.getBoolean(path);
    }
}