package net.propvp.practice.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.propvp.practice.Practice;

public class Config {

	public String fileName;
	public File configFile;
	private FileConfiguration config;

	public Config(String fileName) {
		this.fileName = fileName;
		
		File dataFolder = Practice.getInstance().getDataFolder();
		configFile = new File(dataFolder, fileName);
		
		if(!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			
			if(Practice.getInstance().getResource(fileName) == null) {
				try {
					configFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				Practice.getInstance().saveResource(fileName, false);
			}
        }
		
		config = YamlConfiguration.loadConfiguration(configFile);
	}
	
	public Config(File file, String fileName) {
		configFile = new File(file, fileName);
		
		if(!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			
			if(Practice.getInstance().getResource(fileName) == null) {
				try {
					configFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				Practice.getInstance().saveResource(fileName, false);
			}
        }
		
		config = YamlConfiguration.loadConfiguration(configFile);
	}
	
	public FileConfiguration getConfig() {
		return config;
	}
	
	public void save() {
		try {
			getConfig().save(configFile);
		} catch (IOException e) {
			Bukkit.getLogger().severe("Could not save config file " + configFile.toString());
			e.printStackTrace();
		}
	}
	
}