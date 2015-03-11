package org.apocgaming.endresetold;

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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;
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
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (event.getPlayer().getWorld().getEnvironment() == Environment.THE_END) {
			if (!isDragonKilled) {
				addPlayerToList(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		if (event.getPlayer().getWorld().getEnvironment() == Environment.THE_END) {
			if (!isDragonKilled) {
				removePlayerFromList(event.getPlayer());
			}
		}
	}


	//your listing to the same event
	@EventHandler
	public void onEntityTeleport(PlayerTeleportEvent event) {
		if (event.getTo().getWorld().getEnvironment() == Environment.THE_END && event.getFrom().getWorld().getEnvironment() != Environment.THE_END
				&& event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL) {
			if (isLocked) {
				event.setCancelled(true);
				event.getPlayer().teleport(event.getFrom(), PlayerTeleportEvent.TeleportCause.PLUGIN);
				EndReset.sendMessageToPlayer(event.getPlayer(), "The end is on lockdown.");
			}

			if (!isDragonKilled) {
				addPlayerToList(event.getPlayer());
			}
		} else if (event.getFrom().getWorld().getEnvironment() == Environment.THE_END
				&& event.getTo().getWorld().getEnvironment() != Environment.THE_END) {
			if (!isDragonKilled) {
				removePlayerFromList(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
			isDragonKilled = true;
			EndReset.sendMessageToAllPlayers("The ender dragon has been killed! You can kill the dragon after " + plugin.resetDelay
					+ (plugin.resetDelay == 1 ? " minute" : " minutes" + "."));
			handleExpierence();
			handleTeleport(plugin.tpDelay * 1200, "The end went on lock down! It will be open after " + (plugin.resetDelay - plugin.tpDelay)
					+ ((plugin.resetDelay - plugin.tpDelay) == 1 ? " minute." : " minutes."));
			removeEXPorbs(event.getEntity().getWorld());
			handleWorldRegen(event.getEntity().getWorld(), plugin.resetDelay * 1200);
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

	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		if (event.getWorld().getEnvironment() == Environment.THE_END) {
			plugin.getChunks().add(new APOCChunk(event.getChunk().getX(), event.getChunk().getZ()));
			// EndReset.log.info("Chunk | " + event.getChunk().getX() + " : " +
			// event.getChunk().getZ());
			// EndReset.log.info("Size: " + plugin.getChunks().size());
		}
	}

	private void removePlayerFromList(Player p) {
		plugin.getExpierenceDistributerManager().getContents().remove(p);
	}

	private void addPlayerToList(Player p) {
		if (!plugin.getExpierenceDistributerManager().getContents().containsKey(p)) {
			plugin.getExpierenceDistributerManager().getContents().put(p, 0.0);
			EndReset.sendMessageToPlayer(p, "You have been added to the dragon fight!");
		}
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
			if (expForPerson > 0) {
				EndReset.sendMessageToPlayer(player, "You have been rewarded " + expForPerson + " exp points for doing " + (int) percentDamage
						+ "% of the damage.");
				player.giveExp(expForPerson);
			} else {
				EndReset.sendMessageToPlayer(player, "You did no damage to the ender dragon.");
			}
		}
		if (didMostDamage != null && highestDamage != 0 && plugin.rewardEgg) {
			hasSpace = false;
			for (ItemStack item : didMostDamage.getInventory().getContents()) {
				if (item == null) {
					hasSpace = true;
					didMostDamage.getInventory().addItem(new ItemStack(Material.DRAGON_EGG, 1));
					didMostDamage.sendMessage("\247c[\247bEndReset\247c]\247r You did the most damage to the ender dragon. "
							+ "Therefore you have been rewarded the dragon egg!");
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

	public void handleTeleport(final long delay, final String custom) {
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
						if (delay == 0) {
							EndReset.sendMessageToPlayer(player, custom);
						} else {
							EndReset.sendMessageToAllPlayers(custom);
							isLocked = true;
						}
					}
				}
			}
		}, delay);
	}

	public void removeEXPorbs(final World world) {
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

	}

	public void handleWorld(final World w, final long delay) {
		plugin.regenID = new BukkitRunnable() {
			private int timer;

			@Override
			public void run() {
				timer++;
				if (timer == delay / 1200 || delay == 5 || delay == 0) {
					if (w.getEnvironment() == Environment.THE_END) {
						if (!w.getPlayers().isEmpty()) {
							instantTP("You can't be in the world when it's resetting.");
						}
						for (APOCChunk c : plugin.getChunks()) {
							for (Entity e : w.getChunkAt(c.getX(), c.getZ()).getEntities()) {
								e.remove();
							}
							w.regenerateChunk(c.getX(), c.getZ());
						}
					}
					plugin.getChunks().clear();
					isDragonKilled = false;
					isLocked = false;
					EndReset.sendMessageToAllPlayers("The end has been reset!");
					cancel();
				} else if (timer != 1) {
					String minute = ((delay / 1200) - timer == 1) ? " minute" : " minutes";
					EndReset.sendMessageToAllPlayers("The end will lockdown in " + ((delay / 1200) - timer) + minute + "!");
				}
			}
		}.runTaskTimer(plugin, 0, 1200).getTaskId();
	}

	public void instantTP(String custom) {
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
					if (player.getBedSpawnLocation() == null) {
						loc = player.getCompassTarget();
					} else {
						loc = player.getBedSpawnLocation();
					}
				}
				player.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
				EndReset.sendMessageToPlayer(player, custom);
			}
		}
	}

}
