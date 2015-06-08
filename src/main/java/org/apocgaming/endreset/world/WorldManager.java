package org.apocgaming.endreset.world;

import org.apocgaming.endreset.EndResetPlugin;
import org.apocgaming.endreset.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Created by thomas15v on 11/03/15.
 */
public class WorldManager {

    private GameWorld gameWorld;
    private Location spawnLocation;
    private Config config;
    private EndResetPlugin plugin;

    public WorldManager(EndResetPlugin plugin){
        this.plugin = plugin;
        this.config = plugin.getPluginconfig();
        spawnLocation = getOverWorld().getSpawnLocation();
        this.gameWorld = new GameWorld(getEndWorld(), this, config);
    }

    public World getOverWorld(){
        return getWorld(World.Environment.NORMAL);
    }

    public World getEndWorld(){
        return getWorld(World.Environment.THE_END);
    }

    public GameWorld getGameWorld(){
        return gameWorld;
    }

    public World getWorld(World.Environment environment){
        for (World world: Bukkit.getWorlds())
            if (world.getEnvironment() == environment)
                return world;
        return null;
    }

    public void loadChunk(Chunk chunk){
        if (gameWorld.chunkInWorld(chunk))
            gameWorld.loadChunk(new GameChunk(chunk));
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public EndResetPlugin getPlugin() {
        return plugin;
    }
}
