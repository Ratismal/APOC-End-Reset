package org.apocgaming.endreset.listeners;

import org.apocgaming.endreset.world.WorldManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

/**
 * Created by thomas15v on 11/03/15.
 */
public class WorldListener implements Listener {

    private WorldManager worldManager;

    public WorldListener(WorldManager worldManager){
        this.worldManager = worldManager;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event){
        this.worldManager.loadChunk(event.getChunk());
    }

}
