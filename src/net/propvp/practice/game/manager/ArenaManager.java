package net.propvp.practice.game.manager;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import net.propvp.practice.Practice;
import net.propvp.practice.game.arena.Arena;
import net.propvp.practice.utils.Config;

public class ArenaManager {

	private Config arenaConfig;
	private Map<String, Arena> arenas;

	public ArenaManager() {
		arenaConfig = new Config("arenas.yml");
		arenas = new HashMap<String, Arena>();
		loadArenas();
	}
	
	public FileConfiguration getConfig() {
		return arenaConfig.getConfig();
	}
	
	public void saveConfig() {
		arenaConfig.save();
	}
	
	public Map<String, Arena> getArenas() {
		return arenas;
	}
	
	public boolean arenaExists(String s) {
		return arenas.containsKey(s);
	}

	public Arena getArena(String s) {
		return arenas.get(s);
	}
	
	public Arena getUnusedArena() {
		for(Arena arena : arenas.values()) {
			if(arena.isSetup() && !arena.isActive()) { return arena; }
		}
		
		return null;
	}
	
	public Arena getUnusedMultiArena() {
		for(Arena arena : arenas.values()) {
			if(arena.isSetup() && !arena.isActive() && !arena.isMultiUse()) { return arena; }
		}
		
		return null;
	}

	public void removeArena(String s) {
		arenas.remove(s);
	}

	public void putArena(String s, Arena a) {
		arenas.put(s, a);
	}
	
	public void saveAll() {
		for(Arena a : arenas.values()) {
			a.save();
		}
	}

	public void loadArenas() {
		FileConfiguration config = arenaConfig.getConfig();

		if(config.getConfigurationSection("arenas") == null) {
			Practice.getInstance().getLogger().info("There are no arenas stored in the configuration.");
			return;
		}

		for(String s : config.getConfigurationSection("arenas").getKeys(false)) {
			String sp1 = config.contains("arenas." + s + ".spawn1") ? config.getString("arenas." + s + ".spawn1") : null;
			String sp2 = config.contains("arenas." + s + ".spawn2") ? config.getString("arenas." + s + ".spawn2") : null;
			String c1 = config.contains("arenas." + s + ".region1") ? config.getString("arenas." + s + ".region1") : null;
			String c2 = config.contains("arenas." + s + ".region2") ? config.getString("arenas." + s + ".region2") : null;
			Boolean mU = config.contains("arenas." + s + ".multiuse") ? config.getBoolean("arenas." + s + ".multiuse") : false;
			Arena a = new Arena(s, sp1, sp2, c1, c2, mU);
			arenas.put(s, a);
		};
	}

}