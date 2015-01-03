package org.apocgaming.endreset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Haze on 12/29/2014.
 */
public class EndReset extends JavaPlugin {

	private final ExpierenceDistributerManager expierenceDistributerManager = new ExpierenceDistributerManager();
	private final EndLoadListener endLoadListener = new EndLoadListener(this);
	static Logger log = Logger.getLogger("Minecraft");
	public ConfigManager configmanager = null;
	public final File OUR_FOLDER = new File("plugins\\End Reset\\");
	public final File CRYSTAL_DATA = new File(OUR_FOLDER + "\\CrystalData.txt");
	String seperator = System.getProperty("line.separator");
	public static boolean writtenCrystals = false;

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

	public ExpierenceDistributerManager getExpierenceDistributerManager() {
		return expierenceDistributerManager;
	}

	public static void sendMessageToAllPlayersDebug(String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage("\247c[\247aEndReset [DEBUG]\247c]\247r " + message);
		}
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
		endTPcoords = configmanager.getDoubleArray("end-tp-coord");
		tpDelay = configmanager.getInt("end-tp-out-delay");
		resetDelay = configmanager.getInt("end-reset-time");
		endLockdown = configmanager.getBoolean("end-lockdown");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (commandLabel.equalsIgnoreCase("getPlayers")) {
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
		}
		if (commandLabel.equalsIgnoreCase("getexp")) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(p.getName() + "'s EXP is: " + p.getExp() + "  |  " + p.getExpToLevel() + "  |  " + p.getLevel() + "  |  "
						+ p.getTotalExperience());
				return true;
			}
		}

		if (commandLabel.equalsIgnoreCase("giveexp")) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (args.length == 1) {
					int exp = Integer.parseInt(args[0]);
					p.giveExp(exp);
					p.sendMessage("Gave " + p.getName() + " " + exp + " exp");
					return true;
				} else {
					sender.sendMessage("U failed");
				}
			}
		}

		return false;
	}
}
