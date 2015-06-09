package org.apocgaming.endreset.listeners;

import org.apocgaming.endreset.game.GameHandler;
import org.apocgaming.endreset.util.MessageUtil;
import org.apocgaming.endreset.world.WorldManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Created by thomas15v on 11/03/15.
 */
public class GameListener implements Listener {

    private GameHandler gameHandler;
    private WorldManager worldManager;

    public GameListener(GameHandler gameHandler, WorldManager worldManager) {
        this.gameHandler = gameHandler;
        this.worldManager = worldManager;
    }

    @EventHandler
    public void OnPlayerTeleport(PlayerTeleportEvent event){
        if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END){
            if (worldManager.getGameWorld().isLocked()) {
                event.getPlayer().teleport(worldManager.getSpawnLocation());
                MessageUtil.sendMessage(event.getPlayer(), "The end is in lockdown.");
                event.setCancelled(true);
                event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation());
            }
            else {
                if (!gameHandler.getRunningGame().hasPlayer(event.getPlayer())) {
                    gameHandler.getRunningGame().addPlayer(event.getPlayer());
                    MessageUtil.sendMessage(event.getPlayer(), "Go kill the EnderDragon!");
                }
            }
        }
        else if (event.getFrom().getWorld().getEnvironment() == World.Environment.THE_END)
            gameHandler.getRunningGame().removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onEnderDragonDied(EntityDeathEvent event){
        if (event.getEntity().getType() == EntityType.ENDER_DRAGON)
            gameHandler.stopGame();
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
            Player shooter = null;
            if (event.getDamager() instanceof Player ) {
                shooter = (Player) event.getDamager();
            }else if (event.getDamager() instanceof Arrow) {
                shooter = (Player) ((Arrow) event.getDamager()).getShooter();
            }
            if (shooter != null)
                gameHandler.getRunningGame().getExperienceManager().givePlayer(shooter, (int) event.getDamage());
        }
    }

    @EventHandler
    public void onStupidCatJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getName().equalsIgnoreCase("sentientcat")) {
            MessageUtil.sendMessage(event.getPlayer(), "You smell of poop, cat. Take a bath!");
        }
    }


}
