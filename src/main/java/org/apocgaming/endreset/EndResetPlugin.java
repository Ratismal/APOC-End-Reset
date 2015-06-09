package org.apocgaming.endreset;

import org.apocgaming.endreset.config.Config;
import org.apocgaming.endreset.game.GameHandler;
import org.apocgaming.endreset.listeners.GameListener;
import org.apocgaming.endreset.util.MessageUtil;
import org.apocgaming.endreset.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by thomas15v on 11/03/15.
 */
public class EndResetPlugin extends JavaPlugin
{

    private GameHandler gameHandler;
    private Config pluginconfig;
    private WorldManager worldManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.pluginconfig = new Config(getConfig());
        this.worldManager = new WorldManager(this);
        this.gameHandler = new GameHandler(getPluginconfig(), worldManager);
        getServer().getPluginManager().registerEvents(new GameListener(gameHandler, worldManager), this);
        getLogger().info("APOC End-Reset Enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("[ " + this.getName() + " ] has been disabled.");
    }

    public GameHandler getGameHandler() {
        return gameHandler;
    }

    public Config getPluginconfig() {
        return pluginconfig;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("endreset")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("reset")) {
                    System.out.println("Forcing end reset!");
                    gameHandler.stopGame();

                }
                else if (args[0].equalsIgnoreCase("size")) {
                    System.out.println("Forcing end reset!");
                    World tempworld =  Bukkit.getWorld(pluginconfig.endWorldName());
                    try {
                        MessageUtil.sendMessage(sender, String.valueOf(tempworld.getPlayers().size()));
                    }
                    catch(NullPointerException e){
                        MessageUtil.sendMessage(sender, "World not loaded!");
                    }
                }
                else {
                    MessageUtil.sendMessage(sender, "Invalid command!");
                }
            }
                else {
                sender.sendMessage(ChatColor.BLUE + "/endreset reset   reloads config");
                sender.sendMessage(ChatColor.BLUE + "/endreset size    shows how many people are in the end");
            }

            return true;
        }

        return false;
    }
}
