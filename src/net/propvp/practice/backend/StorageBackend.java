package net.propvp.practice.backend;

import org.bukkit.OfflinePlayer;

import net.propvp.practice.game.GameType;
import net.propvp.practice.player.PlayerData;

public abstract interface StorageBackend {
	
	public abstract void createTables();
	
	public abstract boolean accountExists(OfflinePlayer paramPlayer);
	
	public abstract void ensureAccountExists(OfflinePlayer paramPlayer);
	
	public abstract void saveAccount(PlayerData paramData);
	
	public abstract void loadAccount(PlayerData paramData);
	
	public abstract void insertMatch(OfflinePlayer paramWinner, OfflinePlayer paramLoser, int paramWinnerChange, int paramLoserChange, double elapsedTime);

	public abstract void updateSpecificStats(PlayerData paramData, GameType paramGameType);
	
	public abstract void flushData();
	
}
