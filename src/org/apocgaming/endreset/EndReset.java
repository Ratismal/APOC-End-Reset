package org.apocgaming.endreset;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Haze on 12/29/2014.
 */
public class EndReset extends JavaPlugin {

	public ExpierenceDistributerManager expierenceDistributerManager;
	static Logger log = Logger.getLogger("Minecraft");
	private EndLoadListener endLoadListener;

	public static void sendMessageToAllPlayers(String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage("\247c[\247bEndReset\247c]\247r " + message);
		}
	}


	public void onEnable() {
		expierenceDistributerManager = new ExpierenceDistributerManager();
		expierenceDistributerManager.setup();
		endLoadListener = new EndLoadListener(this);
		getServer().getPluginManager().registerEvents(endLoadListener, this);
		log.info("APOC End-Reset Enabled.");
	}

	public ExpierenceDistributerManager getExpierenceDistributerManager() {
		return expierenceDistributerManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (commandLabel.equalsIgnoreCase("getPlayers")) {
			if (sender instanceof Player) {
				if (args.length == 0) {
					sender.sendMessage("--Beginning to list player data-- ["
							+ getExpierenceDistributerManager().getContents().size() + "] total");
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
				p.sendMessage(p.getName() + "'s EXP is: " + p.getExp() + "  |  " + p.getExpToLevel() + "  |  "
						+ p.getLevel() + "  |  " + p.getTotalExperience());
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

	public static void sendMessageToAllPlayersDebug(String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage("\247c[\247aEndReset [DEBUG]\247c]\247r " + message);
		}
	}
}
