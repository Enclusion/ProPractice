package net.propvp.practice.backend;

import org.bukkit.OfflinePlayer;

import net.propvp.practice.Practice;
import net.propvp.practice.game.GameType;
import net.propvp.practice.player.PlayerData;

public class StorageManager {

	private StorageBackend backend;

	public StorageManager(Practice plugin, StorageBackend backend) {
		this.backend = backend;
	}
	
	public StorageBackend getBackend() {
		return this.backend;
	}
	
	public void createTables() {
		this.backend.createTables();
	}

	public boolean accountExists(OfflinePlayer player) {
		return this.backend.accountExists(player);
	}
	
	public void ensureAccountExists(OfflinePlayer player) {
		this.backend.ensureAccountExists(player);
	}
	
	public void saveAccount(PlayerData data) {
		this.backend.saveAccount(data);
	}
	
	public void loadAccount(PlayerData data) {
		this.backend.loadAccount(data);
	}
	
	public void insertMatch(OfflinePlayer winner, OfflinePlayer loser, int winnerChange, int loserChange, double elapsedTime) {
		this.backend.insertMatch(winner, loser, winnerChange, loserChange, elapsedTime);
	}
	
	public void updateSpecificStats(PlayerData data, GameType gameMode) {
		this.updateSpecificStats(data, gameMode);
	}
	
	public void flushData() {
		this.backend.flushData();
	}

}