package org.apocgaming.endreset.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by thomas15v on 11/03/15.
 */
public class MessageUtil {

    public static void sendMessageToAllPlayers(String message) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(getMessage(message));
        }
    }

    public static void sendMessage(CommandSender sender, String message) {
        if (null != sender) {
            sender.sendMessage(getMessage(message));
        }
    }

    public static String getMessage(String message){
        return new StringBuilder().append("\247c[\247bEndReset\247c]\247r ").append(message).toString();
    }
}
