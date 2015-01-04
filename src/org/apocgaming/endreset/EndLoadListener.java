package org.apocgaming.endreset;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.PortalType;
import org.bukkit.World;
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
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Haze on 12/29/2014.
 */
public class EndLoadListener implements Listener {

	public EndReset plugin;
	private boolean hasSpace = false;
	private boolean isDragonKilled = false;
	private boolean isLocked = false;

	public EndLoadListener(EndReset instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onPortal(PlayerPortalEvent event) {
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL && event.getTo().getWorld().getEnvironment().equals(Environment.THE_END)) {
			if (isLocked) {
				event.setCancelled(true);
				event.getPlayer().teleport(event.getFrom(), PlayerTeleportEvent.TeleportCause.PLUGIN);
				EndReset.sendMessageToPlayer(event.getPlayer(), "The end is on lockdown.");
			}
			if (!isDragonKilled) {
				addToList(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (event.getPlayer().getWorld().getEnvironment() == Environment.THE_END) {
			if (!isDragonKilled) {
				addToList(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		if (event.getPlayer().getWorld().getEnvironment() == Environment.THE_END) {
			if (!isDragonKilled) {
				removeFromList(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onEntityTeleport(PlayerTeleportEvent event) {
		if (event.getTo().getWorld().getEnvironment() == Environment.THE_END && event.getPlayer().getWorld().getEnvironment() != Environment.THE_END) {
			if (isLocked) {
				event.setCancelled(true);
				event.getPlayer().teleport(event.getFrom(), PlayerTeleportEvent.TeleportCause.PLUGIN);
				EndReset.sendMessageToPlayer(event.getPlayer(), "The end is on lockdown.");
			}

			if (!isDragonKilled) {
				addToList(event.getPlayer());
			}
		} else if (event.getFrom().getWorld().getEnvironment() == Environment.THE_END && event.getTo().getWorld().getEnvironment() != Environment.THE_END) {
			if (!isDragonKilled) {
				removeFromList(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
			isDragonKilled = true;
			handleExpierence();
			handleTeleport();
			handleWorldRegen(event);
		}
	}

	@EventHandler
	public void onPortalCreate(EntityCreatePortalEvent event) {
		if (event.getEntityType() == EntityType.ENDER_DRAGON && event.getPortalType() == PortalType.ENDER) {
			event.setCancelled(true);
		}
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

	private void removeFromList(Player p) {
		plugin.getExpierenceDistributerManager().getContents().remove(p);
	}

	private void addToList(Player p) {
		if (!plugin.getExpierenceDistributerManager().getContents().containsKey(p)) {
			plugin.getExpierenceDistributerManager().getContents().put(p, 0.0);
			EndReset.sendMessageToPlayer(p, "You have been added to the dragon fight!");
		}
	}

	private void handleExpierence() {
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

	private void handleTeleport() {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (Player player : plugin.getServer().getOnlinePlayers()) {
					if (player.getWorld().getEnvironment().equals(Environment.THE_END)) {
						Location loc = new Location(plugin.getServer().getWorld(plugin.worldName), plugin.endTPcoords[0], plugin.endTPcoords[1],
								plugin.endTPcoords[2]);
						int iszero = 0;
						for (int i = 0; i < plugin.endTPcoords.length; i++) {
							if (plugin.endTPcoords[i] == 0) {
								iszero++;
							}
						}
						if (iszero == 3) {
							loc = player.getBedSpawnLocation();
						}
						player.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
						EndReset.sendMessageToPlayer(player, "The end went on lock down! After " + (plugin.resetDelay - plugin.tpDelay) + "minutes it will be open again.");
					}
				}
				if (plugin.endLockdown) {
					isLocked = true;
				}
			}
		}, plugin.tpDelay * 1200);
	}

	private void handleWorldRegen(EntityDeathEvent event) {
		final World world = event.getEntity().getWorld();
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
					cancel();
				}
			}
		}.runTaskTimer(plugin, 100, 1);

		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (World w : plugin.getServer().getWorlds()) {
					if (w.getEnvironment() == Environment.THE_END) {
						for (int x = -15; x < 15; x++) {
							for (int z = -15; z < 15; z++) {
								w.loadChunk(x, z);
								w.regenerateChunk(x, z);
								w.unloadChunk(x, z);
							}
						}
					}
				}
				EndReset.sendMessageToAllPlayers("The end has been reset!");
				if (plugin.endLockdown) {
					isLocked = false;
				}
				isDragonKilled = false;
			}
		}, plugin.resetDelay * 1200);
	}
}
