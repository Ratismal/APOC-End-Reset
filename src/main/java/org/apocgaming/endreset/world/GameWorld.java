package org.apocgaming.endreset.world;

import org.apocgaming.endreset.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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

    public GameWorld(World world, WorldManager worldManager){
        this.world = world;
        this.worldManager = worldManager;
        this.modifiedchunks = new HashSet<>();
        //To Make sure we get a dragon at least
        loadChunk(new GameChunk(world.getChunkAt(0,0)));
    }

    public void reset(){
        for (Player player: world.getPlayers())
            player.teleport(worldManager.getSpawnLocation());
        new WorldReseter(modifiedchunks, this).runTaskTimer(worldManager.getPlugin(), 0, 1);
    }

    public boolean chunkInWorld(Chunk chunk){
        return world.getUID().equals(chunk.getWorld().getUID());
    }

    public void loadChunk(GameChunk chunk){
        if (!beingreset && !this.modifiedchunks.contains(chunk))
            this.modifiedchunks.add(chunk);
    }

    public void unloadChunk(Chunk chunk) {
        if (!beingreset)
            this.modifiedchunks.remove(chunk);
    }

    public void lock(int minutes){
        locked = true;
        minutesleft = minutes;
        runTaskTimer(worldManager.getPlugin(), 0, 1200);
    }

    @Override
    public int hashCode() {
        return world.getUID().hashCode();
    }

    public boolean isLocked() {
        return locked;
    }

    @Override
    public void run() {
        if (minutesleft == 0) {
            locked = false;
            cancel();
            MessageUtil.sendMessageToAllPlayers("The end is unlocked !!!");
        }
        else {
            MessageUtil.sendMessageToAllPlayers("The end will lockdown in " + minutesleft + " minute(s) !");
            minutesleft--;
        }
    }

    public World getWorld() {
        return world;
    }

    public void setBeingreset(boolean beingreset) {
        this.beingreset = beingreset;
    }
}
