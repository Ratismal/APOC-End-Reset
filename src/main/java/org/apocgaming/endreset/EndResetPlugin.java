package org.apocgaming.endreset;

import org.apocgaming.endreset.config.Config;
import org.apocgaming.endreset.game.GameHandler;
import org.apocgaming.endreset.listeners.GameListener;
import org.apocgaming.endreset.listeners.WorldListener;
import org.apocgaming.endreset.world.WorldManager;
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
        getServer().getPluginManager().registerEvents(new WorldListener(worldManager), this);
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
}
