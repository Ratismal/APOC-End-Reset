package org.apocgaming.endreset.world;

import org.apocgaming.endreset.util.MessageUtil;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by thomas15v on 11/03/15.
 */
public class WorldReseter extends BukkitRunnable {

    private World world;
    private Set<GameChunk> chunks;
    private GameWorld gameWorld;
    private Iterator<GameChunk> chunkIterator;

    public WorldReseter(Set<GameChunk> chunks, GameWorld gameWorld){
        this.chunks = chunks;
        this.gameWorld = gameWorld;
        this.world = gameWorld.getWorld();
        this.chunkIterator = chunks.iterator();
    }

    @Override
    public void run() {
        if (chunkIterator.hasNext()) {
            GameChunk chunk = chunkIterator.next();
            world.regenerateChunk(chunk.getX(), chunk.getZ());
            world.refreshChunk(chunk.getX(), chunk.getZ());
            System.out.println("generating " + chunk +  " with " + chunks.size() + " chunks left");
            chunkIterator.remove();

        }else {
            MessageUtil.sendMessageToAllPlayers("The end has been reset!");
            cancel();
        }

    }
}
