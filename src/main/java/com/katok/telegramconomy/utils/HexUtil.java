package com.katok.telegramconomy.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HexUtil {
    public static String color(String message) {
        if (message == null) {
            return "";
        }

        Pattern pattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String color = matcher.group(1);
            String replacement = ChatColor.of("#" + color).toString();
            message = message.replace("&#" + color, replacement);
        }
        message = message.replace("\n", "\n");
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    public static Component Component_HEX(String message) {
        if (message == null) {
            return Component.empty();
        }
        Component result = Component.empty();

        Pattern pattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = pattern.matcher(message);

        List<String> texts = new ArrayList<>();
        List<String> colors = new ArrayList<>();
        while(matcher.find()) {
            String color = matcher.group(1);
            StringBuilder text = new StringBuilder("");

            for(int index_in_text = 0; index_in_text < message.length(); index_in_text++) {
                if(message.charAt(index_in_text) == '&') {
                    if(message.substring(index_in_text, index_in_text + 8).equals("&#" + color)) {
                        message = message.substring(8 + text.length());
                        break;
                    }
                }
                text.append(message.charAt(index_in_text));
            }
            colors.add("#" + color);
            texts.add(text.toString().replaceAll("&#([A-Fa-f0-9]{6})", ""));
        }

        texts.add(message);

        result = result.append(Component.text(texts.get(0)));

        for(int index_in_texts = 1; index_in_texts < texts.size(); index_in_texts++) {
            result = result.append(Component.text(texts.get(index_in_texts)).color(TextColor.fromCSSHexString(colors.get(index_in_texts - 1))));
        }

        return result;
    }
}