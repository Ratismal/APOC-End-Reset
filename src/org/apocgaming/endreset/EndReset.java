package org.apocgaming.endreset;

import java.io.BufferedWriter;
import java.io.File;
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
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Haze on 12/29/2014.
 */
public class EndReset extends JavaPlugin {

	public ExpierenceDistributerManager expierenceDistributerManager;
	static Logger log = Logger.getLogger("Minecraft");
	private EndLoadListener endLoadListener;
	public final File PLUGIN_FOLDER = new File("plugins");
	public final File DATA_FILE = new File("APOC-EndReset-CrystalData.txt");
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
		expierenceDistributerManager = new ExpierenceDistributerManager();
		expierenceDistributerManager.setup();
		endLoadListener = new EndLoadListener(this);
		getServer().getPluginManager().registerEvents(endLoadListener, this);
		log.info("APOC End-Reset Enabled.");
	}


public void saveChrystalLocations(World w){
        int helper = 0;
        String seperator =  System.getProperty("line.separator");
        try{
            log.info("Trying to make a new file. [" + DATA_FILE.getPath() + "]");
            if(!DATA_FILE.exists()){
                DATA_FILE.createNewFile();
                log.info("Created new File.");
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(DATA_FILE.getAbsoluteFile()));
            out.write("######################" + seperator);
            out.write("# This is a configuration file that marks where the Ender Crystals are so we can respawn them when the end 'resets'." + seperator);
            out.write("######################" + seperator);
                    for(Entity e : w.getEntities()){
                        if(e.getType()==EntityType.ENDER_CRYSTAL){
                            out.write("Crystal No " + helper + ": " + e.getLocation().getX() + ", " + e.getLocation().getY() + ", " + e.getLocation().getZ() + seperator);
                            helper++;

                        }
                    }
            writtenCrystals = true;
            out.close();
        }catch (IOException e){
            e.printStackTrace();
        }
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
}
