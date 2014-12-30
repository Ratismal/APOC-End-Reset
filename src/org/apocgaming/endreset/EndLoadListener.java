package org.apocgaming.endreset;

import net.minecraft.server.v1_6_R3.EntityArrow;
import net.minecraft.server.v1_6_R3.EntityLiving;
import net.minecraft.server.v1_6_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftArrow;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftEnderDragon;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.Map;

/**
 * Created by Haze on 12/29/2014.
 */
public class EndLoadListener implements Listener {


    public EndReset plugin;
    private boolean isInEnd = false;

    public EndLoadListener(EndReset instance)
    {
        this.plugin = instance;
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event){
        if(event.getCause()== PlayerTeleportEvent.TeleportCause.END_PORTAL){
            addToList(event.getPlayer());
        }
    }

    private void addToList(Player p){
        plugin.getExpierenceDistributerManager().getContents().put(p,0.0);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
      //  if(event.getEntity().getType()==EntityType.ENDER_DRAGON){
            handleExpierence();
      //  }
    }

    private void handleExpierence(){
        double total = 0;
        for(Map.Entry e : plugin.getExpierenceDistributerManager().getContents().entrySet()){
            total+=(double)e.getValue();
        }
        double expForPerson = 0;
        for(Map.Entry e : plugin.getExpierenceDistributerManager().getContents().entrySet()){
            //someone do this for me
            /*
            Just get the amount of damage, get the percent out of the total
            then you want to get that % out of the dragons health
            then once you have that %, award the player with the xP levels.
             */
            expForPerson = (double)e.getValue()/total;
            Player player = (Player) e.getKey();
            player.sendMessage("\247c[\247bEndReset\247c]\247r You have been rewarded " + expForPerson + " exp points for doing " + ((double)e.getValue() + "").replaceAll("0.","")+"% of the damage.");
            player.setExp((float) (player.getExp() + expForPerson));
        }
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageByEntityEvent event){
        if(event.getEntity().getType()==EntityType.ENDER_DRAGON) {
            CraftPlayer shooter = null;
            if (event.getDamager().getType() == EntityType.ARROW) {
                CraftArrow damager = (CraftArrow) event.getDamager();
                if (damager.getShooter().getType() == EntityType.PLAYER) {
                    shooter = (CraftPlayer) damager.getShooter();
                }
            } else {
                shooter = (CraftPlayer) event.getDamager();
            }
            double oldDamage = plugin.getExpierenceDistributerManager().getContents().get(shooter.getPlayer());
            plugin.getExpierenceDistributerManager().getContents().put(shooter.getPlayer(),oldDamage+event.getDamage());
        }
    }

}
