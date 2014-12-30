package org.apocgaming.endreset;

import net.minecraft.server.v1_6_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.world.WorldLoadEvent;

/**
 * Created by Haze on 12/29/2014.
 */
public class EndLoadListener implements Listener {


    public EndReset plugin;
    private boolean isInEnd = false;

    public EndLoadListener(EndReset instance)
    {
        this.plugin = instance;
        plugin.getLogger().info("End Load Listener init.");
    }

    @EventHandler
    public void onWorldLoaded(WorldLoadEvent event){
        /* Check if the End world is being loaded.
        * Might not work rn.
        * */
        boolean shouldEnable = false;
         if(event.getWorld().getEnvironment()==World.Environment.THE_END){
            /* Loop through the entities */
            for(Entity e : event.getWorld().getEntities()) {
                /* Check if a player */
                if(e instanceof EntityPlayer) {
                    /* Cast the player and add to the list */
                    Player p = Bukkit.getPlayer(((EntityPlayer) e).getName());
                    plugin.getExpierenceDistributerManager().getContents().put(p, 0.0);
                    shouldEnable = true;
                    plugin.getLogger().info("som1 is in da end br90!");
                }
            }
             if(shouldEnable){
                 isInEnd = true;
             }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event){
       // if(isInEnd){
            boolean hasBeenSaid = false;
            if(event.getEntity() instanceof EnderDragon && hasBeenSaid==false){
                plugin.getLogger().info("yo the enderdragoon is here zil.");
                EndReset.sendMessageToAllPlayers("[APOC-ER] The EnderDragon has been found!");
            }
       // }
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageByEntityEvent event){
       // if(isInEnd){
            plugin.getLogger().info("EY x U ATTACKIN SHIT FOR WHAT REASON.");
            if(event.getCause()==EntityDamageEvent.DamageCause.PROJECTILE){
                Entity proj = event.getDamager();
                /* GET THE OWNER OF THE DAMN ARROW */
            } else if(event.getCause()== EntityDamageEvent.DamageCause.ENTITY_ATTACK && event.getDamager() instanceof Player){
                Player attacker = (Player) event.getDamager();
                // this is deleteding and adding everytime a player attaks lelelelelelekek
                double oldDMG = plugin.getExpierenceDistributerManager().getContents().get(event.getDamager());
                plugin.getExpierenceDistributerManager().getContents().remove(attacker);
                plugin.getExpierenceDistributerManager().getContents().put(attacker,oldDMG);
            }
        //}
    }

}
