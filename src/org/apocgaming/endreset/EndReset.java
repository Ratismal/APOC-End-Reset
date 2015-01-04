package org.apocgaming.endreset;

import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
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
		switch (commandLabel.toLowerCase()) {
		case "listplayers":
			if (sender instanceof Player) {
				if (args.length == 0) {
					sender.sendMessage("--Beginning to list player data-- [" + getExpierenceDistributerManager().getContents().size() + "] total");
					for (Map.Entry e : this.getExpierenceDistributerManager().getContents().entrySet()) {
						sender.sendMessage("Player [" + e.getKey() + "] has a dmg value of " + e.getValue());
					}
					return true;
				} else {
					sender.sendMessage("Please don't use any arguments.");
					return true;
				}
			}
			break;
		case "clearend":
			endLoadListener.handleTeleport();
			sender.sendMessage("Teleported all players out of the End.");
			break;
		case "resetend":
			for (World w : getServer().getWorlds()) {
				if (w.getEnvironment() == World.Environment.THE_END) {
					endLoadListener.handleWorldRegen(w);
					break;
				}
			}
			break;
		default:
			return false;
		}
		return false;
	}
}
