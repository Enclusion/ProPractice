package net.propvp.practice.backend.type;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.OfflinePlayer;

import net.propvp.practice.DatabaseCredentials;
import net.propvp.practice.Practice;
import net.propvp.practice.backend.StorageBackend;
import net.propvp.practice.backend.connection.MySQLConnection;
import net.propvp.practice.game.GameType;
import net.propvp.practice.player.PlayerData;

public class StorageBackendMySQL implements StorageBackend {

	private MySQLConnection dbConn;

	public StorageBackendMySQL(DatabaseCredentials dbCredentials) {
		this.dbConn = new MySQLConnection(dbCredentials);
	}

	public void createTables() {
		try (Connection conn = this.dbConn.openConnection()) {
			Practice.getInstance().getLogger().info("Creating tables...");
			conn.prepareStatement("CREATE TABLE IF NOT EXISTS `propractice_players` (`player_identifier` CHAR(36) KEY, `total_wins` INT(11) DEFAULT 0, `total_losses` INT(11) DEFAULT 0)").executeUpdate();
			conn.prepareStatement("CREATE TABLE IF NOT EXISTS `propractice_matches` (`match_identifier` CHAR(36) NOT NULL UNIQUE, `winner_identifier` CHAR(36) NOT NULL, `loser_identifier` CHAR(36) NOT NULL, `winner_change` INT(11) NOT NULL, `loser_change` INT(11) NOT NULL, `date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, `elapsed_time` DECIMAL NOT NULL)").executeUpdate();
			conn.prepareStatement("CREATE TABLE IF NOT EXISTS `propractice_schema` (`key` VARCHAR(32) PRIMARY KEY, `val` TEXT)").executeUpdate();

			if(Practice.getInstance().getGameManager() == null) return;
			if(Practice.getInstance().getGameManager().getGameTypes() == null) return;
			if(Practice.getInstance().getGameManager().getGameTypes().isEmpty()) return;

			for(String name : Practice.getInstance().getGameManager().getGameTypes().keySet()) {
				PreparedStatement ps = conn.prepareStatement("ALTER TABLE `propractice_players` ADD COLUMN (" + name + "_rating INT(11) DEFAULT 1000," + name + "_wins INT(11) DEFAULT 0," + name + "_losses INT(11) DEFAULT 0)");
				ps.executeUpdate();
			}

			Practice.getInstance().getLogger().info("Created tables...");
		} catch (SQLException e) {
			if(!e.getMessage().toLowerCase().contains("duplicate")) {
				e.printStackTrace();
			}
		}
	}

	public MySQLConnection getConnection() {
		return this.dbConn;
	}

	@Override
	public synchronized boolean accountExists(OfflinePlayer paramPlayer) {
		try {
			PreparedStatement statement = this.dbConn.openConnection().prepareStatement("SELECT 1 FROM `propractice_players` WHERE `player_identifier` = ?");
			statement.setString(1, paramPlayer.getUniqueId().toString());
			ResultSet rs = statement.executeQuery();
			return rs.next();
		} catch (Exception e) {
			throw new RuntimeException("SQL error has occurred.", e);
		}
	}

	@Override
	public synchronized void ensureAccountExists(OfflinePlayer paramPlayer) {
		this.dbConn.executeAsyncOperation(conn -> {
			try {
				PreparedStatement statement = conn.prepareStatement("INSERT INTO `propractice_players` (player_identifier) VALUES (?)");
				statement.setString(1, paramPlayer.getUniqueId().toString());
				statement.executeUpdate();
			} catch (Exception e) {
				if(!e.getMessage().toLowerCase().contains("duplicate")) {
					throw new RuntimeException("SQL error has occurred.", e);
				}
			}
		});
	}

	@Override
	public synchronized void loadAccount(PlayerData data) {
		this.dbConn.executeAsyncOperation(conn -> {
			try {
				this.ensureAccountExists(data.getPlayer());
				PreparedStatement statement = conn.prepareStatement("SELECT * FROM `propractice_players` WHERE `player_identifier`=?");
				statement.setString(1, data.getPlayer().getUniqueId().toString());
				ResultSet result = statement.executeQuery();

				if(result.next()) {
					for(Entry<String, GameType> games : Practice.getInstance().getGameManager().getGameTypes().entrySet()) {
						data.setRating(games.getValue(), result.getInt(games.getKey() + "_rating"));
						data.setWins(games.getValue(), result.getInt(games.getKey() + "_wins"));
						data.setLosses(games.getValue(), result.getInt(games.getKey() + "_losses"));
					}
				}
			} catch (SQLException e) {
				throw new RuntimeException("SQL error has occurred.");
			}
		});
	}

	@Override
	public synchronized void saveAccount(PlayerData data) {
		this.dbConn.executeAsyncOperation(conn -> {
			try {
				this.ensureAccountExists(data.getPlayer());

				StringBuilder query = new StringBuilder();
				query.append("UPDATE `propractice_players` SET total_wins=?,total_losses=?,");

				int size = Practice.getInstance().getGameManager().getGameTypes().size();
				int i = 0;

				if(size == 0) return;

				for(String name : Practice.getInstance().getGameManager().getGameTypes().keySet()) {
					i++;

					if(i == size) {
						query.append(name + "_wins=?," + name + "_losses=?," + name + "_rating=?");
					} else {
						query.append(name + "_wins=?," + name + "_losses=?," + name + "_rating=?,");
					}
				}

				query.append(" WHERE `player_identifier` = ?");

				PreparedStatement statement = conn.prepareStatement(query.toString());

				i = 2;

				for(GameType game : Practice.getInstance().getGameManager().getGameTypes().values()) {
					i++;
					statement.setInt(i, data.getWins(game));
					i++;
					statement.setInt(i, data.getLosses(game));
					i++;
					statement.setInt(i, data.getRating(game).getRating());
				}

				statement.setInt(1, data.getTotalWins());
				statement.setInt(2, data.getTotalLosses());
				statement.setString(size * 3 + 3, data.getPlayer().getUniqueId().toString());
				statement.executeUpdate();
			} catch (Exception e) {
				throw new RuntimeException("SQL error has occurred.");
			}
		});
	}

	@Override
	public synchronized void insertMatch(OfflinePlayer winner, OfflinePlayer loser, int winnerChange, int loserChange, double elapsedTime) {
		this.dbConn.executeAsyncOperation(conn -> {
			try {
				PreparedStatement statement = conn.prepareStatement("INSERT INTO `propractice_matches` (match_identifier, winner_identifier, loser_identifier, winner_change, loser_change, elapsed_time) VALUES (?,?,?,?,?,?)");
				statement.setString(1, UUID.randomUUID().toString());
				statement.setString(2, winner.getUniqueId().toString());
				statement.setString(3, loser.getUniqueId().toString());
				statement.setInt(4, winnerChange);
				statement.setInt(5, loserChange);
				statement.setDouble(6, elapsedTime);
				statement.executeUpdate();
			} catch (Exception e) {
				throw new RuntimeException("SQL error has occured.", e);
			}
		});
	}

	@Override
	public synchronized void updateSpecificStats(PlayerData data, GameType gameMode) {
		this.dbConn.executeAsyncOperation(conn -> {
			try {
				this.ensureAccountExists(data.getPlayer());

				PreparedStatement statement = conn.prepareStatement("UPDATE `propractice_players` SET " + gameMode.getName() + "_wins=?, " + gameMode.getName() + "_losses=?, " + gameMode.getName() + "_ratings=? WHERE player_identifier = ?");
				statement.setInt(1, data.getWins(gameMode));
				statement.setInt(2, data.getLosses(gameMode));
				statement.setInt(3, data.getRating(gameMode).getRating());
				statement.setString(4, data.getPlayer().getUniqueId().toString());
				statement.executeUpdate();
			} catch (Exception e) {
				throw new RuntimeException("SQL error has occured.", e);
			}
		});
	}

	@Override
	public synchronized void flushData() {
		this.dbConn.executeAsyncOperation(conn -> {
			try {
				conn.prepareStatement("DROP TABLE IF EXISTS `propractice_players`;").executeUpdate();
				conn.prepareStatement("DROP TABLE IF EXISTS `propractice_matches`;").executeUpdate();
				conn.prepareStatement("DROP TABLE IF EXISTS `propractice_schema`;").executeUpdate();
			} catch (Exception e) {
				throw new RuntimeException("SQL error has occured.", e);
			}
		});
	}

}