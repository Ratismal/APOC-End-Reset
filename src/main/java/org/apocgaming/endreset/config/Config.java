package org.apocgaming.endreset.config;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

	private int totalExp = 22075;
	private boolean rewardEgg = true;
	private double[] endTPcoords = new double[] { 0, 0, 0 };
	private int tpDelay = 10;
	private int resetDelay = 30;
	private FileConfiguration config = null;
	
	public Config(FileConfiguration config) {
		this.config = config;
		reload();
	}

	public void reload() {
		totalExp = getConfig().getInt("total-exp");
		rewardEgg = getConfig().getBoolean("reward-egg");
		tpDelay = getConfig().getInt("end-tp-out-delay");
		resetDelay = getConfig().getInt("end-reset-time");
	}

	private double[] getDoubleArray(String name) {
		double[] array = new double[]{getConfig().getDoubleList(name).size()};
		for(int i = 0; i < array.length; i++) {
			array[i] = getConfig().getDoubleList(name).get(i);
		}
		return array;
	}

	public int getTotalExp() {
		return totalExp;
	}

	public boolean isRewardEgg() {
		return rewardEgg;
	}


	public double[] getEndTPcoords() {
		return endTPcoords;
	}

	public int getTpDelay() {
		return tpDelay;
	}

	public int getResetDelay() {
		return resetDelay;
	}

	public FileConfiguration getConfig() {
		return config;
	}
}
