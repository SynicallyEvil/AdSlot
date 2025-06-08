package me.synicallyevil.adslot.utils;

import org.bukkit.ChatColor;

import java.util.List;

public class Utils {

    public static String getColor(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static boolean isNumber(String s){
        return s.matches("\\d+");
    }
}
