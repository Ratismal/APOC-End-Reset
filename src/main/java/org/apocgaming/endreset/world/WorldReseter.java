package org.apocgaming.endreset.world;

import org.apocgaming.endreset.util.MessageUtil;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
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
    public void run() { //World reseter
        if (chunkIterator.hasNext()) { //There are more chunks to reset
            GameChunk chunk = chunkIterator.next();
            //world.loadChunk(chunk.getX(), chunk.getZ());
            world.regenerateChunk(chunk.getX(), chunk.getZ());

            world.refreshChunk(chunk.getX(), chunk.getZ());
            //world.unloadChunk(chunk.getX(), chunk.getZ());
            System.out.println("generating " + chunk +  " with " + chunks.size() + " chunks left");
            chunkIterator.remove();

        }else { //World has been reset
            MessageUtil.sendMessageToAllPlayers("The end has been reset!");
            gameWorld.unlock();
            cancel();
        }

    }


}
