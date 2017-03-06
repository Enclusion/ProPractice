package net.propvp.practice;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import net.propvp.practice.backend.StorageBackend;
import net.propvp.practice.backend.StorageManager;
import net.propvp.practice.backend.type.StorageBackendFlatfile;
import net.propvp.practice.backend.type.StorageBackendMySQL;
import net.propvp.practice.utils.Config;
import net.propvp.practice.utils.LocationUtil;

public class PracticeConfiguration {

	private static Practice plugin;
	private static Logger logger;
	private static Config rootConfig;
	private static Config msgConfig;
	private static StorageManager storage;
	private static Location spawnLocation;
	private static Location editorLocation;

	private static boolean isLimitedMatches;
	private static int limitedMatchesAmount;

	public PracticeConfiguration() {
		plugin = Practice.getInstance();
		logger = plugin.getLogger();

		rootConfig = new Config("config.yml");
		msgConfig = new Config("messages.yml");

		boolean updatedConfig = false;

		if(!rootConfig.getConfig().contains("items.player-options") || !rootConfig.getConfig().contains("menus.player-options")) {
			rootConfig.getConfig().set("menus.player-options", "&cPlayer Options");
			rootConfig.getConfig().set("items.player-options.name", "&cPlayer Options");
			rootConfig.getConfig().set("items.player-options.lore", Arrays.asList("&7Right-click to open the", "&7player options menu."));
			rootConfig.getConfig().set("items.player-options.material", "REDSTONE_COMPARATOR");
			rootConfig.getConfig().set("items.player-options.data", "0");
			rootConfig.save();
			updatedConfig = true;
		}

		if(!rootConfig.getConfig().contains("practice.limited-matches")) {
			rootConfig.getConfig().set("practice.limited-matches", true);
			rootConfig.save();
			updatedConfig = true;
		}

		if(!rootConfig.getConfig().contains("practice.limited-matches-amount")) {
			File playerDataFolder = new File(Practice.getInstance().getDataFolder().toString() + File.separator + "playerdata");
			
			if(playerDataFolder.exists()) {
				if(playerDataFolder.listFiles().length != 0) {
					logger.info("Updating all player configuration files. This could take a while, please wait patiently...");
					
					for(File playerFile : playerDataFolder.listFiles()) {
						try {
							Config cfg = new Config(playerFile, playerFile.getName());
							cfg.getConfig().set("data.limited-matches", 10);
							cfg.save();
							cfg = null;
							logger.info("Successfully updated player configuration file " + playerFile.getName());
						} catch(Exception e) {
							logger.severe("Failed to update player configuration file " + playerFile.getName());
							continue;
						}
					}
				}
			} else {
				logger.severe("playerdata file doesn't exist. " + playerDataFolder.toString());
			}
			
			rootConfig.getConfig().set("practice.limited-matches-amount", 10);
			rootConfig.save();
			updatedConfig = true;
		}

		if(msgConfig.getConfig().contains("queue.match-limit-reached")) {
			msgConfig.getConfig().set("queue.match-limit-reached", null);
			msgConfig.getConfig().set("queue.match-limit-remove", null);
			msgConfig.save();
			updatedConfig = true;
		}

		if(!msgConfig.getConfig().contains("limited-matches")) {
			msgConfig.getConfig().set("limited-matches.match-limit-reached", "&cYou have 0 ranked matches remaining.");
			msgConfig.getConfig().set("limited-matches.match-limit-notification", "&cYou have $amountRemaining ranked matches remaining for today.");
			msgConfig.getConfig().set("limited-matches.match-limit-advertisement", "&cTo remove this limitation you need to purchase a donator rank.");
			msgConfig.save();
			updatedConfig = true;
		}

		if(updatedConfig) {
			logger.info("Updated old configuration file to the new configuration file.");
		}

		if(rootConfig.getConfig().contains("practice.spawnloc")) {
			spawnLocation = LocationUtil.getLocation(rootConfig.getConfig().getString("practice.spawnloc"));
		} else {
			logger.severe("You do not have your spawn point setup.");
		}

		if(rootConfig.getConfig().contains("practice.editorloc")) {
			editorLocation = LocationUtil.getLocation(rootConfig.getConfig().getString("practice.editorloc"));
		} else {
			logger.severe("You do not have your editor point setup.");
		}

		isLimitedMatches = rootConfig.getConfig().getBoolean("practice.limited-matches");
		limitedMatchesAmount = rootConfig.getConfig().getInt("practice.limited-matches-amount");
	}

	public static void loadBackend() {
		logger.info("Initializing storage backend...");
		StorageBackend backend = loadBackend(rootConfig.getConfig().getConfigurationSection("backend"));

		if(backend == null) {
			logger.severe("Failed to load backend!");
			return;
		}

		storage = new StorageManager(plugin, backend);
	}

	private static StorageBackend loadBackend(ConfigurationSection config) {
		String backendType = config.getString("type");
		StorageBackend backend = null;

		if(backendType.equalsIgnoreCase("flatfile")) {
			//String backendFileName = config.getString("file", "practice.db");
			backend = new StorageBackendFlatfile();
			logger.info("Initialized Flatfile backend.");
			logger.info("No need to test flatfile backend.");
		} else {
			if(!backendType.equalsIgnoreCase("mysql")) {
				logger.severe("Unknown storage backend " + backendType + "!");
				return null;
			}

			StorageBackendMySQL mySQLBackend = new StorageBackendMySQL(loadCredentials(config));

			logger.info("Initialized MySQL backend.");
			logger.info("Testing connection...");

			if (!mySQLBackend.getConnection().testConnection()) {
				logger.severe("MySQL connection failed - cannot continue!");
				plugin.shutdown();
				return null;
			} else {
				backend = mySQLBackend;
			}

			logger.info("Connection successful!");
		}

		return backend;
	}

	private static DatabaseCredentials loadCredentials(ConfigurationSection config) {
		String backendHost = config.getString("host");
		int backendPort = config.getInt("port", 3306);
		String backendDb = config.getString("database");
		String backendUser = config.getString("username");
		String backendPass = config.getString("password");

		return new DatabaseCredentials(backendHost, backendPort, backendUser, backendPass, backendDb);
	}

	public static StorageManager getStorage() {
		return storage;
	}

	public static Config getRootConfig() {
		return rootConfig;
	}

	public static Config getMessageConfig() {
		return msgConfig;
	}

	public static Location getSpawnLocation() {
		return spawnLocation;
	}

	public static void setSpawnLocation(Location loc) {
		spawnLocation = loc;
	}

	public static boolean isSpawnSet() {
		return (spawnLocation == null) ? false : true;
	}

	public static Location getEditorLocation() {
		return editorLocation;
	}

	public static void setEditorLocation(Location loc) {
		editorLocation = loc;
	}

	public static boolean isEditorSet() {
		return (editorLocation == null) ? false : true;
	}

	public static void teleportToSpawn(Player player) {
		if(spawnLocation != null) {
			player.teleport(spawnLocation);
		} else {
			logger.severe("Attempted to teleport player to the spawn point but it's not set.");
		}
	}

	public static void teleportToEditor(Player player) {
		if(editorLocation != null) {
			player.teleport(editorLocation);
		} else {
			logger.severe("Attempted to teleport player to the editor point but it's not set.");
		}
	}

	public static void saveLocations() {
		if(spawnLocation != null) {
			rootConfig.getConfig().set("practice.spawnloc", LocationUtil.getString(spawnLocation));
		}

		if(editorLocation != null) {
			rootConfig.getConfig().set("practice.editorloc", LocationUtil.getString(editorLocation));
		}

		rootConfig.save();
	}

	public static boolean isLimitedMatches() {
		return isLimitedMatches;
	}

	public static int getLimitedMatchesAmount(){
		return limitedMatchesAmount;
	}

}