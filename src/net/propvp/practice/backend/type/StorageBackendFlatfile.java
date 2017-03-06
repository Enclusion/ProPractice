package net.propvp.practice.backend.type;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import net.propvp.practice.Practice;
import net.propvp.practice.backend.StorageBackend;
import net.propvp.practice.game.GameType;
import net.propvp.practice.player.PlayerData;
import net.propvp.practice.utils.Config;

public class StorageBackendFlatfile implements StorageBackend {

	private Config configuration;

	public StorageBackendFlatfile() {
		configuration = new Config("database.yml");
	}

	public void createTables() {
		FileConfiguration config = configuration.getConfig();
		if(!config.isConfigurationSection("propractice_players")) { config.createSection("propractice_players"); }
		if(!config.isConfigurationSection("propractice_matches")) { config.createSection("propractice_matches"); }
		if(!config.isConfigurationSection("propractice_schema")) { config.createSection("propractice_schema"); }

		config.set("propractice_schema.version", "3.0.5");

		configuration.save();
	}

	@Override
	public boolean accountExists(OfflinePlayer paramPlayer) {
		return configuration.getConfig().contains("propractice_players." + paramPlayer.getUniqueId().toString()) ? true : false;
	}

	@Override
	public void ensureAccountExists(OfflinePlayer paramPlayer) {
		FileConfiguration config = configuration.getConfig();
		String playerString = "propractice_players." + paramPlayer.getUniqueId().toString();

		config.set(playerString + ".username", paramPlayer.getName());
		config.set(playerString + ".identifier", paramPlayer.getUniqueId().toString());
		config.set(playerString + ".first-played", paramPlayer.getFirstPlayed());

		if(!config.contains(playerString + ".limited-matches")) {
			config.set(playerString + ".limited-matches", 10);
		}

		configuration.save();
	}

	@Override
	public void loadAccount(PlayerData data) {
		FileConfiguration config = configuration.getConfig();
		String playerString = "propractice_players." + data.getPlayer().getUniqueId().toString();

		if(!config.isConfigurationSection(playerString)) {
			this.ensureAccountExists(data.getPlayer());
		}


		boolean needsToSave = false;

		if(config.get(playerString + ".gametype_ratings") == null) {
			needsToSave = true;
		} else if(config.isConfigurationSection(playerString + ".gametype_ratings")) {
			for(String gameType : config.getConfigurationSection(playerString + ".gametype_ratings").getKeys(false)) {
				if(!Practice.getInstance().getGameManager().gameTypeExists(gameType)) {
					needsToSave = true;
				} else {
					try {
						data.setRating(Practice.getInstance().getGameManager().getGameType(gameType), config.getInt(playerString + ".gametype_ratings." + gameType));
					} catch (Exception e) {
						needsToSave = true;
					}
				}
			}
		} else {
			needsToSave = true;
		}

		if(config.get(playerString + ".gametype_wins") == null) {
			needsToSave = true;
		} else if(config.isConfigurationSection(playerString + ".gametype_wins")) {
			for(String gameType : config.getConfigurationSection(playerString + ".gametype_wins").getKeys(false)) {
				if(!Practice.getInstance().getGameManager().gameTypeExists(gameType)) {
					needsToSave = true;
				} else {
					try {
						data.setWins(Practice.getInstance().getGameManager().getGameType(gameType), config.getInt(playerString + ".gametype_wins." + gameType));
					} catch (Exception e) {
						needsToSave = true;
					}
				}
			}
		} else {
			needsToSave = true;
		}

		if(config.get(playerString + ".gametype_losses") == null) {
			needsToSave = true;
		} else if(config.isConfigurationSection(playerString + ".gametype_losses")) {
			for(String gameType : config.getConfigurationSection(playerString + ".gametype_losses").getKeys(false)) {
				if(!Practice.getInstance().getGameManager().gameTypeExists(gameType)) {
					needsToSave = true;
				} else {
					try {
						data.setLosses(Practice.getInstance().getGameManager().getGameType(gameType), config.getInt(playerString + ".gametype_losses." + gameType));
					} catch (Exception e) {
						needsToSave = true;
					}
				}
			}
		} else {
			needsToSave = true;
		}

		if(needsToSave) {
			this.saveAccount(data);
		}
	}

	@Override
	public void saveAccount(PlayerData data) {
		FileConfiguration config = configuration.getConfig();
		String playerString = "propractice_players." + data.getPlayer().getUniqueId().toString();

		config.set(playerString + ".username", data.getPlayer().getName());
		config.set(playerString + ".identifier", data.getPlayer().getUniqueId().toString());
		config.set(playerString + ".first-played", data.getPlayer().getFirstPlayed());

		if(Practice.getInstance().getGameManager().getGameTypes().isEmpty()) return;

		for(Entry<String, GameType> gt : Practice.getInstance().getGameManager().getGameTypes().entrySet()) {
			config.set(playerString + ".gametype_wins." + gt.getKey(), data.getWins(gt.getValue()));
			config.set(playerString + ".gametype_losses." + gt.getKey(), data.getLosses(gt.getValue()));
			config.set(playerString + ".gametype_ratings." + gt.getKey(), data.getRating(gt.getValue()).getRating());
		}

		configuration.save();
	}

	@Override
	public void insertMatch(OfflinePlayer winner, OfflinePlayer loser, int winnerChange, int loserChange, double elapsedTime) {
		FileConfiguration config = configuration.getConfig();
		UUID identifier = UUID.randomUUID();
		String matchString = "propractice_matches." + identifier.toString();

		config.set(matchString + ".identifier", identifier.toString());
		config.set(matchString + ".timestamp", new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
		config.set(matchString + ".elapsed_time", elapsedTime);

		config.createSection(matchString + ".winner");
		config.set(matchString + ".winner.username", winner.getName());
		config.set(matchString + ".winner.identifier", winner.getUniqueId().toString());
		config.set(matchString + ".winner.elo_change", winnerChange);

		config.createSection(matchString + ".loser");
		config.set(matchString + ".loser.username", loser.getName());
		config.set(matchString + ".loser.identifier", loser.getUniqueId().toString());
		config.set(matchString + ".loser.elo_change", loserChange);

		configuration.save();
	}

	@Override
	public void updateSpecificStats(PlayerData data, GameType gameMode) {
		FileConfiguration config = configuration.getConfig();
		String playerString = "propractice_players." + data.getPlayer().getUniqueId().toString();

		for(Entry<String, GameType> gt : Practice.getInstance().getGameManager().getGameTypes().entrySet()) {
			config.set(playerString + ".gametype_wins." + gt.getKey(), data.getWins(gt.getValue()));
			config.set(playerString + ".gametype_losses." + gt.getKey(), data.getLosses(gt.getValue()));
			config.set(playerString + ".gametype_ratings." + gt.getKey(), data.getRating(gt.getValue()).getRating());
		}

		configuration.save();
	}

	@Override
	public void flushData() {
		for(String key : configuration.getConfig().getKeys(false)) {
			configuration.getConfig().set(key, null);
		}

		configuration.save();
	}

}