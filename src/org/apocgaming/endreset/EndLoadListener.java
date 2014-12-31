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
import org.bukkit.event.player.PlayerJoinEvent;
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
            if(!EndReset.writtenCrystals){
                plugin.saveChrystalLocations(event.getPlayer());
            }
            addToList(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        if(event.getPlayer().getWorld().getEnvironment()== World.Environment.THE_END){
                if(!EndReset.writtenCrystals){
                    plugin.saveChrystalLocations(event.getPlayer());
                }
                addToList(event.getPlayer());
            }
    }

    private void addToList(Player p) {
        plugin.getExpierenceDistributerManager().getContents().put(p, 0.0);
        EndReset.sendMessageToAllPlayersDebug(p.getName() + " has joined dragon fight!");
    }

    private void handleExpierence() {
        double totalDamageDone = 0;
        int totalExpForEveryBody = 22075;
        for (Map.Entry e : plugin.getExpierenceDistributerManager().getContents().entrySet()) {
            totalDamageDone += (double) e.getValue();
        }
        double highestDamage = 0;
        Player didMostDamage = null;
        for (Map.Entry e : plugin.getExpierenceDistributerManager().getContents().entrySet()) {
            Player player = (Player) e.getKey();
            double percentDamage = 100 / totalDamageDone * (double) e.getValue();
            int expForPerson = (int) (percentDamage / 100 * totalExpForEveryBody);
            if (highestDamage < percentDamage) {
                highestDamage = percentDamage;
                didMostDamage = player;
            }
            player.sendMessage("\247c[\247bEndReset\247c]\247r You have been rewarded " + expForPerson
                    + " exp points for doing " + (int) percentDamage + "% of the damage.");
            player.giveExp(expForPerson);
        }
        if (didMostDamage != null && highestDamage != 0) {
            boolean hasSpace = false;
            //untested
            for (ItemStack item : didMostDamage.getInventory().getContents()) {
                if (item == null) {
                    hasSpace = true;
                    didMostDamage.getInventory().addItem(new ItemStack(Material.DRAGON_EGG, 1));
                    didMostDamage.sendMessage("\247c[\247bEndReset\247c]\247r You did the most damage to the ender dragon. "
                            + "There for you have been rewared the dragon egg!");
                    break;
                }
            }
            if (!hasSpace) {
                didMostDamage.getWorld().dropItem(didMostDamage.getLocation(), new ItemStack(Material.DRAGON_EGG, 1));
                didMostDamage.sendMessage("\247c[\247bEndReset\247c]\247r You did the most damage to the ender dragon. "
                        + "Your invetory is full, the dragon egg dropped on the ground.");
            }
        }

    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if(event.getEntity().getType()==EntityType.ENDER_DRAGON){
            handleExpierence();
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
