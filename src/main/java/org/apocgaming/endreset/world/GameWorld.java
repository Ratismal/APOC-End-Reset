package org.apocgaming.endreset.world;

import org.apocgaming.endreset.config.Config;
import org.apocgaming.endreset.util.MessageUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by thomas15v on 11/03/15.
 */
public class GameWorld extends BukkitRunnable {

    private World world;
    private WorldManager worldManager;
    private Set<GameChunk> modifiedchunks;
    private boolean locked = false;
    private int minutesleft = 0;
    private boolean beingreset = false;
    private Config config;


    public GameWorld(World world, WorldManager worldManager, Config config) {
        //this.world = Bukkit.getWorld(config.endWorldName());
        this.worldManager = worldManager;
        this.modifiedchunks = new HashSet<>();
        this.config = config;
        //To Make sure we get a dragon at least
        loadChunk(new GameChunk(world.getChunkAt(0, 0)));
    }

    public void reset() {
        world = Bukkit.getWorld(config.endWorldName());
        System.out.println(world.getPlayers().size());
        for (Player player : world.getPlayers()) {
            MessageUtil.sendMessage(player, "End is resetting!");
            //player.sendMessage("We gotta get you out of here!");

            Location spawn = worldManager.getSpawnLocation();
            Location spawnLocation = new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ());
            //System.out.println("Teleporting to " + spawn.getWorld().toString() + " " + spawn.getX() + " " + spawn.getY() + " " + spawn.getZ());
            player.teleport(spawnLocation);
        }
        try {
            resetWorld();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean chunkInWorld(Chunk chunk) {
        return world.getUID().equals(chunk.getWorld().getUID());
    }

    public void loadChunk(GameChunk chunk) {
        if (!beingreset && !this.modifiedchunks.contains(chunk))
            this.modifiedchunks.add(chunk);
    }

    public void unloadChunk(Chunk chunk) {
        if (!beingreset)
            this.modifiedchunks.remove(chunk);
    }

    /*
    public void lock(int minutes){
        locked = true;
        minutesleft = minutes;
        runTaskTimer(worldManager.getPlugin(), 0, 1200);
    }
    */
    public void lock() {
        locked = true;
    }

    public void unlock() {
        locked = false;
    }

    @Override
    public int hashCode() {
        return world.getUID().hashCode();
    }

    public boolean isLocked() {
        return locked;
    }

    @Override
    public void run() { //Send message to players, for whether the end is locked or not
        if (!isLocked()) {
            //locked = false;
            cancel();
            MessageUtil.sendMessageToAllPlayers("The end is unlocked !!!");
        } else {
            //MessageUtil.sendMessageToAllPlayers("The end will lockdown in " + minutesleft + " minute(s) !");
            MessageUtil.sendMessageToAllPlayers("The end is in lockdown!");
            //minutesleft--;
        }
    }

    public World getWorld() {
        return world;
    }

    public void setBeingreset(boolean beingreset) {
        this.beingreset = beingreset;
    }

    public void resetWorld() {
        world = Bukkit.getWorld(config.endWorldName());
        System.out.println("[APOC-End-Reset] Deleting world folder...");
        Bukkit.unloadWorld(world, true);
        WorldType worldtype = world.getWorldType();
        World.Environment worldenvironment = world.getEnvironment();
        String worldname = world.getName();

        File deleteFolder = world.getWorldFolder();

        deleteWorld(deleteFolder);
        System.out.println("[APOC-End-Reset] World folder deleted.");
        /*
        WorldCreator worldcreator = new WorldCreator(worldname);
        worldcreator.type(worldtype);
        worldcreator.environment(worldenvironment);
        worldcreator.generateStructures(true);
        Bukkit.createWorld(worldcreator);
        */
        //System.out.println("World created. Done!");
        unlock();
    }

    public boolean deleteWorld(File path) {
        if (path.exists()) {
            File files[] = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteWorld(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }
}
