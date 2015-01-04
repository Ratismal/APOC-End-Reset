package org.apocgaming.endreset;

import java.util.Map;
import java.util.Random;

import net.minecraft.server.v1_6_R3.Block;
import net.minecraft.server.v1_6_R3.EntityEnderDragon;

import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftArrow;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Haze on 12/29/2014.
 */
public class EndLoadListener implements Listener {

	public EndReset plugin;
	private boolean hasSpace = false;
	private boolean isDragonKilled = false;

	public EndLoadListener(EndReset instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onPortal(PlayerPortalEvent event) {
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL && event.getTo().getWorld().getEnvironment().equals(Environment.THE_END)) {
			// for (Entity e : event.getTo().getWorld().getEntities()) {
			// if (e.getType() == EntityType.ENDER_DRAGON) {
			if (!isDragonKilled) {
				addToList(event.getPlayer());
			}
			// break;
			// }
			// }
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (event.getPlayer().getWorld().getEnvironment() == Environment.THE_END) {
			// for (Entity e : event.getPlayer().getWorld().getEntities()) {
			// if (e.getType() == EntityType.ENDER_DRAGON) {
			if (!isDragonKilled) {
				addToList(event.getPlayer());
			}
			// break;
			// }
			// }
		}
	}

	private void addToList(Player p) {
		plugin.getExpierenceDistributerManager().getContents().put(p, 0.0);
		EndReset.sendMessageToAllPlayersDebug(p.getName() + " has joined dragon fight!");
	}

	public void handleExpierence() {
		double totalDamageDone = 0;
		int totalExpForEveryBody = plugin.totalExp;
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
			player.sendMessage("\247c[\247bEndReset\247c]\247r You have been rewarded " + expForPerson + " exp points for doing "
					+ (int) percentDamage + "% of the damage.");
			player.giveExp(expForPerson);
		}
		if (didMostDamage != null && highestDamage != 0 && plugin.rewardEgg) {
			hasSpace = false;
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
		plugin.getExpierenceDistributerManager().getContents().clear();
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
			isDragonKilled = true;
			handleExpierence();
			handleTeleport();
			handleWorldRegen(event.getEntity().getWorld());
		}
	}

	@EventHandler
	public void onPortalCreate(EntityCreatePortalEvent event) {
		if (event.getEntityType() == EntityType.ENDER_DRAGON && event.getPortalType() == PortalType.ENDER) {
			event.setCancelled(true);
		}
	}

	public void handleWorldRegen(World worldx) {
		final World world = worldx;
		EndReset.sendMessageToAllPlayers("Trying to reload the world..");
		try {
			new BukkitRunnable() {
				private int timer;

				@Override
				public void run() {
					timer++;
					for (Entity e : world.getEntities()) {
						if (e.getType() == EntityType.EXPERIENCE_ORB) {
							e.remove();
						}
					}
					if (timer > 150) {
						EndReset.log.info("Cancelled!");
						cancel();
					}
				}
			}.runTaskTimer(plugin, 100, 1);

			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					for (World w : plugin.getServer().getWorlds()) {
						if (w.getEnvironment() == Environment.THE_END) {
							for (int x = -50; x < 50; x++) {
								for (int z = -50; z < 50; z++) {
									w.loadChunk(x, z);
									w.regenerateChunk(x, z);
									w.unloadChunk(x, z);
								}
							}
						}
					}
					isDragonKilled = false;
				}
			}, 620 /* plugin.tpDelay * 1200 - 20 */);
		} catch (Exception e){
			EndReset.sendMessageToAllPlayers("Failed! " + e.getCause() + " MSG : " + e.getMessage());
		}
		EndReset.sendMessageToAllPlayers("Finished reloading the End!");
	}


	public void handleTeleport() {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (Player player : plugin.getServer().getOnlinePlayers()) {
					if (player.getWorld().getEnvironment().equals(Environment.THE_END)) {
						EndReset.sendMessageToAllPlayers("You have been teleported out of the end!");
						player.teleport(player.getBedSpawnLocation(), TeleportCause.PLUGIN);
					}
				}
			}
		}, 600 /* plugin.tpDelay * 1200 */);
	}

	@EventHandler
	public void onEntityDamaged(EntityDamageByEntityEvent event) {
		if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
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
			plugin.getExpierenceDistributerManager().getContents().put(shooter.getPlayer(), oldDamage + event.getDamage());
		}
	}

}
