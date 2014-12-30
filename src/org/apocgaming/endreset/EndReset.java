package org.apocgaming.endreset;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
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
			p.sendMessage(message);
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
					sender.sendMessage("--Beginning to list player data--");
					for (Iterator<Map.Entry<Player, Double>> iterator = this.getExpierenceDistributerManager().getContents()
							.entrySet().iterator(); iterator.hasNext();) {
						Entry<Player, Double> e = iterator.next();
						sender.sendMessage("Player [" + e.getKey() + "] has a dmg value of " + e.getValue());
					}
					return true;
				} else {
					sender.sendMessage("Please don't use any arguments.");
					return true;
				}
			}
		}
		return false;
	}
}
