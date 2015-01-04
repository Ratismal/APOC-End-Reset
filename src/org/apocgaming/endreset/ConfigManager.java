package org.apocgaming.endreset;

import java.util.Arrays;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

		private FileConfiguration config = null;
	
		public ConfigManager(FileConfiguration config) {
			this.config = config;
		}
	
		public boolean getBoolean(String name) {
			return config.getBoolean(name);
		}
		
		public int getInt(String name) {
			return config.getInt(name);
		}
		
		public double getDouble(String name) {
			return config.getDouble(name);
		}

		public String getString(String name) {
			return config.getString(name);
		}
		
		public double[] getDoubleArray(String name) {
			double[] array = new double[]{config.getDoubleList(name).size()};
			for(int i = 0; i < array.length; i++) {
				array[i] = config.getDoubleList(name).get(i);
				EndReset.log.info(Double.toString(array[i]));
			}
			return array;
		}
}
