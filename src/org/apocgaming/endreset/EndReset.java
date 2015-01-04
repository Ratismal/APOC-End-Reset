package org.apocgaming.endreset;

import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Haze on 12/29/2014.
 */
public class EndReset extends JavaPlugin {

	private final ExpierenceDistributerManager expierenceDistributerManager = new ExpierenceDistributerManager();
	private final EndLoadListener endLoadListener = new EndLoadListener(this);
	static Logger log = Logger.getLogger("Minecraft");
	public ConfigManager configmanager = null;
	private String version = "1.0";
	public int totalExp = 22075;
	public boolean rewardEgg = true;
	public String worldName = "Spawn";
	public double[] endTPcoords = new double[] { 0, 25, 0 };
	public int tpDelay = 10;
	public int resetDelay = 30;
	public boolean endLockdown = true;

	public static void sendMessageToAllPlayers(String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage("\247c[\247bEndReset\247c]\247r " + message);
		}
	}

	public static void sendMessageToPlayer(Player player, String message) {
		if (null != player) {
			player.sendMessage("\247c[\247bEndReset\247c]\247r " + message);
		}
	}

	public ExpierenceDistributerManager getExpierenceDistributerManager() {
		return expierenceDistributerManager;
	}

	public void onEnable() {
		expierenceDistributerManager.setup();
		getServer().getPluginManager().registerEvents(endLoadListener, this);
		saveDefaultConfig();
		configmanager = new ConfigManager(getConfig());
		loadConfig();
		log.info("APOC End-Reset Enabled.");
	}

	private void loadConfig() {
		totalExp = configmanager.getInt("total-exp");
		rewardEgg = configmanager.getBoolean("reward-egg");
		worldName = configmanager.getString("end-tp-world");
		endTPcoords[0] = configmanager.getDouble("end-tp-X");
		endTPcoords[1] = configmanager.getDouble("end-tp-Y");
		endTPcoords[2] = configmanager.getDouble("end-tp-Z");
		tpDelay = configmanager.getInt("end-tp-out-delay");
		resetDelay = configmanager.getInt("end-reset-time");
		endLockdown = configmanager.getBoolean("end-lockdown");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (command.getLabel().equalsIgnoreCase("apocer")) {
			if (args.length == 0) {
				sendMessageToSender(sender, "/apocer" + ChatColor.BLUE + " | Shows all the commands for Apoc-End-Reset");
				sendMessageToSender(sender, "/apocer clear" + ChatColor.BLUE + " | Will teleport all the players out of the end.");
				sendMessageToSender(sender, "/apocer reset" + ChatColor.BLUE + " | Will reset the end.");
				sendMessageToSender(sender, "/apocer version" + ChatColor.BLUE + " | Shows the plugin version.");
				sendMessageToSender(sender, "/apocer reload" + ChatColor.BLUE + " | reloads the plugin.");
				sendMessageToSender(sender, "Programmed by Capsar & Freakyfalse of the Apocalyptic Gaming Network");
				sendMessageToSender(sender, "http://apocgaming.org");
				sendMessageToSender(sender, "https://github.com/Zilacon/APOC-End-Reset");
			} else {
				if (args[0].equalsIgnoreCase("clear")) {
					endLoadListener.handleTeleport(0);
					sendMessageToSender(sender, "Teleported all players out of the End.");
				} else if (args[0].equalsIgnoreCase("reset")) {
					for (World w : getServer().getWorlds()) {
						if (w.getEnvironment() == World.Environment.THE_END) {
							endLoadListener.handleWorldRegen(w, 0);
							sendMessageToSender(sender, "The end is resetting.");
							break;
						}
					}
				} else if (args[0].equalsIgnoreCase("version")) {
					sendMessageToSender(sender, "APOC End-Reset Plugin | Version " + this.version);
					sendMessageToSender(sender, "Programmed by Capsar & Freakyfalse of the Apocalyptic Gaming Network");
					sendMessageToSender(sender, "http://apocgaming.org");
					sendMessageToSender(sender, "https://github.com/Zilacon/APOC-End-Reset");
				} else if (args[0].equalsIgnoreCase("reload")) {
					this.getServer().getPluginManager().getPlugin(this.getName()).getPluginLoader().disablePlugin(this);
					this.getServer().getPluginManager().getPlugin(this.getName()).getPluginLoader().enablePlugin(this);
					sendMessageToSender(sender, "Plugin has been reloaded.");
				}
			}
			return true;
		}
		return false;
	}

	private void sendMessageToSender(CommandSender sender, String message) {
		if (null != sender) {
			sender.sendMessage("\247c[\247bEndReset\247c]\247r " + message);
		}
	}
}
