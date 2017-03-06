package net.propvp.practice.game.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import net.propvp.practice.Practice;
import net.propvp.practice.game.GameType;
import net.propvp.practice.game.matchmaking.MatchMaker;
import net.propvp.practice.game.matchmaking.NormalMatchMaker;
import net.propvp.practice.game.matchmaking.RankedMatchMaker;
import net.propvp.practice.utils.Config;
import net.propvp.practice.utils.InventoryUtil;

public class GameManager {

	private Config gameConfig;
	private Map<String, GameType> gameTypes;
	private Map<UUID, MatchMaker> matchMakers;

	public GameManager() {
		gameConfig = new Config("gametypes.yml");
		gameTypes = new HashMap<String, GameType>();
		matchMakers = new HashMap<UUID, MatchMaker>();
		loadGameTypes();
	}
	
	public FileConfiguration getConfig() {
		return gameConfig.getConfig();
	}
	
	public void saveConfig() {
		gameConfig.save();
	}
	
	public Map<UUID, MatchMaker> getMatchMakers() {
		return matchMakers;
	}
	
	public void addToMatchMaker(UUID uuid, Object obj) {
		MatchMaker matchMaker = matchMakers.get(uuid);
		matchMaker.addObject(obj);
	}
	
	public MatchMaker getMatchMaker(UUID uuid) {
		return matchMakers.get(uuid);
	}
	
	public int getGameMenuAmount() {
		if(gameTypes.size() < 9) {
			return 9;
		} else if(gameTypes.size() > 9 && gameTypes.size() < 19) {
			return 18;
		} else if(gameTypes.size() > 18 && gameTypes.size() < 28) {
			return 27;
		} else if(gameTypes.size() > 27 && gameTypes.size() < 37) {
			return 36;
		} else {
			return 45;
		}
	}
	
	public Map<String, GameType> getGameTypes() {
		return gameTypes;
	}

	public void saveGameTypes() {
		gameTypes.values().forEach(gm -> gm.save());
	}
	
	public boolean gameTypeExists(String s) {
		return gameTypes.containsKey(s);
	}

	public GameType getGameType(String s) {
		return gameTypes.get(s);
	}

	public void removeGameType(String s) {
		gameTypes.get(s).remove();
		gameTypes.remove(s);
	}

	public void putGameType(String s, GameType gm) {
		gameTypes.put(s, gm);
	}
	
	public void loadGameTypes() {
		FileConfiguration config = gameConfig.getConfig();

		if(config.getConfigurationSection("gametypes") == null) {
			Practice.getInstance().getLogger().info("There are no game types stored in the configuration.");
			return;
		}
		
		for(String s : config.getConfigurationSection("gametypes").getKeys(false)) {
			GameType gm = new GameType(s);
			
			if(!config.contains("gametypes." + s + ".display-name")) {
				gm.setDisplayName(s);
			} else {
				gm.setDisplayName(config.getString("gametypes." + s + ".display-name"));
			}

			try {
				gm.setStartingInventory(InventoryUtil.playerInventoryFromString(config.getString("gametypes." + s + ".items")));
			} catch (ArrayIndexOutOfBoundsException e) {
				continue;
			}
				
			if(config.contains("gametypes." + s + ".display-icon.material") && config.contains("gametypes." + s + ".display-icon.data")) {
				if(config.getString("gametypes." + s + ".display-icon.data").chars().allMatch(Character::isDigit)) {
					gm.setIcon(new ItemStack(Material.getMaterial(config.getString("gametypes." + s + ".display-icon.material")), 1, (short) config.getInt("gametypes." + s + ".display-icon.data")));
				}
			}

			if (config.getString("gametypes." + s + ".display-order") != null) {
				if(config.getString("gametypes." + s + ".display-icon.data").chars().allMatch(Character::isDigit)) {
					gm.setDisplayOrder(config.getInt("gametypes." + s + ".display-order"));
				}
			}

			if (config.getString("gametypes." + s + ".editable") != null) {
				gm.setEditable(config.getBoolean("gametypes." + s + ".editable"));
			}

			if(config.getString("gametypes." + s + ".regeneration") != null) {
				gm.setRegeneration(config.getBoolean("gametypes." + s + ".regeneration"));
			}

			if(config.getString("gametypes." + s + ".hunger") != null) {
				gm.setHunger(config.getBoolean("gametypes." + s + ".hunger"));
			}

			if(config.getString("gametypes." + s + ".building") != null) {
				gm.setBuilding(config.getBoolean("gametypes." + s + ".building"));
			}
			
			if(config.getString("gametypes." + s + ".breaking") != null) {
				gm.setBreaking(config.getBoolean("gametypes." + s + ".breaking"));
			}

			if(config.contains("gametypes." + s + ".hit-delay")) {
				gm.setHitDelay(config.getInt("gametypes." + s + ".hit-delay"));
			} else {
				config.set("gametypes." + s + ".hit-delay", 20);
				gm.setHitDelay(20);
			}

			gameTypes.put(s, gm);

			MatchMaker ranked = new RankedMatchMaker(gm, true);
			MatchMaker normal = new NormalMatchMaker(gm, false);
			MatchMaker normalP = new NormalMatchMaker(gm, true);

			matchMakers.put(ranked.getIdentifier(), ranked);
			matchMakers.put(normal.getIdentifier(), normal);
			matchMakers.put(normalP.getIdentifier(), normalP);
		}
	}

}