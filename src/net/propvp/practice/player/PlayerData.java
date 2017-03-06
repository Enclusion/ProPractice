package net.propvp.practice.player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Scoreboard;

import net.propvp.practice.Practice;
import net.propvp.practice.PracticeConfiguration;
import net.propvp.practice.game.Game;
import net.propvp.practice.game.GameType;
import net.propvp.practice.game.duel.DuelInfo;
import net.propvp.practice.game.matchmaking.MatchMaker;
import net.propvp.practice.party.Party;
import net.propvp.practice.scoreboard.ScoreboardHelper;
import net.propvp.practice.scoreboard.ScoreboardUser;
import net.propvp.practice.timing.ElapsedTimer;
import net.propvp.practice.utils.Config;
import net.propvp.practice.utils.InventoryUtil;
import net.propvp.practice.utils.ItemBuilder;
import net.propvp.practice.utils.MessageUtils;

public class PlayerData {

	private Config dataConfig;

	private Player player;
	private Game game;
	private MatchMaker matchMaker;
	private Party party;

	private Map<String, ElapsedTimer> timers;
	private Map<UUID, DuelInfo> duelInvites;

	private Map<GameType, PlayerElo> gameRatings;
	private Map<GameType, Integer> gameWins;
	private Map<GameType, Integer> gameLosses;
	private Map<GameType, Map<String, PlayerKit>> gameKits;

	private ScoreboardHelper scoreboardHelper;
	private ScoreboardUser scoreboardUser;

	private int limitedMatches = PracticeConfiguration.getLimitedMatchesAmount();

	private boolean isSpectating = false;
	private boolean allowSpectators = false;
	private boolean hidingSounds = false;
	private boolean hidingPlayers = false;
	private boolean hidingScoreboard = false;
	private boolean hidingRequests = false;

	public PlayerData(Player player) {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		player.setScoreboard(board);

		File dataFile = new File(Practice.getInstance().getDataFolder() + "/playerdata/" + player.getUniqueId().toString() + ".yml");
		dataConfig = new Config(dataFile, player.getUniqueId().toString() + ".yml");

		this.player = player;
		this.duelInvites = new HashMap<UUID, DuelInfo>();

		this.gameRatings = new HashMap<GameType, PlayerElo>();
		this.gameWins = new HashMap<GameType, Integer>();
		this.gameLosses = new HashMap<GameType, Integer>();
		this.gameKits = new HashMap<GameType, Map<String, PlayerKit>>();

		this.scoreboardHelper = new ScoreboardHelper(board, MessageUtils.color(PracticeConfiguration.getRootConfig().getConfig().getString("scoreboard.title")));
		this.scoreboardUser = new ScoreboardUser(player);

		Practice.getInstance().getGameManager().getGameTypes().values().forEach(gameType -> {
			gameKits.put(gameType, new HashMap<String, PlayerKit>());
			gameRatings.put(gameType, new PlayerElo(1000));
			gameWins.put(gameType, 0);
			gameLosses.put(gameType, 0);
		});

		this.loadAccount();
	}

	public Player getPlayer() {
		return player;
	}

	public Game getMatch() {
		return game;
	}

	public MatchMaker getMatchMaker() {
		return matchMaker;
	}

	public Party getParty() {
		return party;
	}

	public void setMatch(Game match) {
		this.game = match;
	}

	public void setMatchMaker(MatchMaker matchMaker) {
		this.matchMaker = matchMaker;
	}

	public void setParty(Party party) {
		this.party = party;
	}

	public boolean inMatchmaking() {
		return (matchMaker != null);
	}

	public boolean inParty() {
		return (party != null);
	}

	public boolean inMatch() {
		return (game != null);
	}

	public boolean hasDuelInvite(Player player) {
		return duelInvites.containsKey(player.getUniqueId());
	}

	public void addDuelInvite(Player player, DuelInfo info) {
		duelInvites.put(player.getUniqueId(), info);
	}

	public void removeDuelInvite(Player player) {
		duelInvites.remove(player.getUniqueId());
	}

	public DuelInfo getDuelInvite(Player player) {
		return duelInvites.get(player.getUniqueId());
	}

	public boolean containsTimer(String key) {
		return timers.containsKey(key);
	}

	public void addTimer(String key, ElapsedTimer timer) {
		timers.put(key, timer);
	}

	public void removeTimer(String key) {
		timers.remove(key);
	}

	public ElapsedTimer getTimer(String key) {
		return timers.get(key);
	}

	public PlayerElo getRating(GameType gm) {
		return gameRatings.get(gm);
	}

	public Map<GameType, PlayerElo> getRatings() {
		return gameRatings;
	}

	public void setRating(GameType gm, int amount) {
		if(gameRatings.containsKey(gm)) {
			gameRatings.replace(gm, new PlayerElo(amount));
		} else {
			gameRatings.put(gm, new PlayerElo(amount));
		}
	}

	public Integer getWins(GameType gm) {
		return gameWins.get(gm);
	}

	public void addWin(GameType gm) {
		gameWins.put(gm, gameWins.get(gm) + 1);
	}

	public void setWins(GameType gm, Integer amount) {
		if(gameWins.containsKey(gm)) {
			gameWins.replace(gm, amount);
		} else {
			gameWins.put(gm, amount);
		}
	}

	public Integer getLosses(GameType gm) {
		return gameLosses.get(gm);
	}

	public void addLoss(GameType gm) {
		gameLosses.put(gm, gameLosses.get(gm) + 1);
	}

	public void setLosses(GameType gm, Integer amount) {
		if(gameLosses.containsKey(gm)) {
			gameLosses.replace(gm, amount);
		} else {
			gameLosses.put(gm, amount);
		}
	}

	public int getTotalWins() {
		if(gameWins.isEmpty()) return 0;
		int i = 0;

		for(Integer wins : gameWins.values()) {
			i = i + wins;
		}

		return i;
	}

	public int getTotalLosses() {
		if(gameLosses.isEmpty()) return 0;
		int i = 0;

		for(Integer wins : gameLosses.values()) {
			i = i + wins;
		}

		return i;
	}

	public void showKits(Player ply, GameType gameType) {
		PlayerInventory inv = ply.getInventory();
		inv.setItem(0, new ItemBuilder(Material.ENCHANTED_BOOK, ChatColor.GOLD + "Default " + gameType.getName() + " Kit", "").getItem());
		ply.updateInventory();
	}

	public PlayerKit getKit(GameType gameType, String name) {
		for(Entry<String, PlayerKit> entry : this.gameKits.get(gameType).entrySet()) {
			if(entry.getKey().equalsIgnoreCase(name)) return entry.getValue();
		}

		return null;
	}

	public PlayerKit getKit(GameType gameType, UUID uuid) {
		for(Entry<String, PlayerKit> entry : this.gameKits.get(gameType).entrySet()) {
			if(entry.getValue().getUUID().equals(uuid)) return entry.getValue();
		}

		return null;
	}

	public void saveKit(GameType gameType, PlayerKit kit) {
		if(this.gameKits.get(gameType).containsKey(kit.getName())) {
			gameKits.get(gameType).replace(kit.getName(), kit);
		} else {
			gameKits.get(gameType).put(kit.getName(), kit);
		}

		this.dataConfig.getConfig().set("kits." + gameType.getName() + "." + kit.getName() + ".inv", InventoryUtil.playerInventoryToString(kit.getInv()));
		this.dataConfig.save();
	}

	public void removeKit(GameType gameType, PlayerKit kit) {
		if(this.gameKits.get(gameType).containsKey(kit.getName())) {
			this.gameKits.get(gameType).remove(kit.getName());
		}

		this.dataConfig.getConfig().set("kits." + gameType.getName() + "." + kit.getName(), null);
		this.dataConfig.save();
	}

	public boolean hasKit(GameType gameType, String name) {
		if(this.gameKits.get(gameType).isEmpty()) return false;

		for(Entry<String, PlayerKit> entry : this.gameKits.get(gameType).entrySet()) {
			if(entry.getKey().equalsIgnoreCase(name)) return true;
		}

		return false;
	}

	public boolean isSpectating() {
		return isSpectating;
	}

	public void setSpectating(boolean bool) {
		this.isSpectating = bool;
	}

	public boolean isAllowSpectators() {
		return allowSpectators;
	}

	public void setAllowSpectators(boolean hideSounds) {
		this.allowSpectators = hideSounds;
	}

	public boolean isHidingSounds() {
		return hidingSounds;
	}

	public void setHidingSounds(boolean hideSounds) {
		this.hidingSounds = hideSounds;
	}

	public boolean isHidingScoreboard() {
		return hidingScoreboard;
	}

	public void setHidingScoreboard(boolean hidingScoreboard) {
		this.hidingScoreboard = hidingScoreboard;
	}

	public boolean isHidingPlayers() {
		return hidingPlayers;
	}

	public void setHidingPlayers(boolean hidingPlayers) {
		this.hidingPlayers = hidingPlayers;
	}

	public boolean isHidingRequests() {
		return hidingRequests;
	}

	public void setHidingRequests(boolean hidingRequests) {
		this.hidingRequests = hidingRequests;
	}

	public ScoreboardHelper getScoreboardHelper() {
		return scoreboardHelper;
	}

	public ScoreboardUser getScoreboardUser() {
		return scoreboardUser;
	}

	public int getLimitedMatchesRemaining() {
		return limitedMatches;
	}

	public void decrementLimitedMatches() {
		this.limitedMatches = this.limitedMatches - 1;

		if(!this.dataConfig.getConfig().contains("data.limited-matches-start")) {
			this.dataConfig.getConfig().set("data.limited-matches-start", System.currentTimeMillis());
		}
		
		this.dataConfig.getConfig().set("data.limited-matches", this.limitedMatches);
		this.dataConfig.save();
	}

	public void loadAccount() {
		PracticeConfiguration.getStorage().ensureAccountExists(player);
		PracticeConfiguration.getStorage().loadAccount(this);

		if(!this.dataConfig.getConfig().contains("data.limited-matches")) {
			this.dataConfig.getConfig().set("data.limited-matches", this.limitedMatches);
			this.dataConfig.save();
		} else {
			this.limitedMatches = this.dataConfig.getConfig().getInt("data.limited-matches");
		}
		
		if(this.dataConfig.getConfig().contains("data.limited-matches-start")) {
			Long startDate = this.dataConfig.getConfig().getLong("data.limited-matches-start");
			
			if((System.currentTimeMillis() - startDate) >= TimeUnit.DAYS.toMillis(1)) {
				this.dataConfig.getConfig().set("data.limited-matches", PracticeConfiguration.getLimitedMatchesAmount());
				this.dataConfig.getConfig().set("data.limited-matches-start", null);
				this.dataConfig.save();
			}
		}

		if(PracticeConfiguration.isLimitedMatches() && !player.hasPermission("practice.limited-matches.bypass")) {
			if(this.limitedMatches <= 0) {
				player.sendMessage(MessageUtils.getTranslation("limited-matches.match-limit-reached"));
			} else {
				player.sendMessage(MessageUtils.getTranslation("limited-matches.match-limit-notification").replace("$amountRemaining", this.limitedMatches + ""));
			}
			
			player.sendMessage(MessageUtils.getTranslation("limited-matches.match-limit-advertisement"));
		}

		if(!this.dataConfig.getConfig().contains("kits")) return;
		if(!this.dataConfig.getConfig().isConfigurationSection("kits")) return;
		if(this.dataConfig.getConfig().get("kits") == null) return;

		if(this.dataConfig.getConfig().contains("kits")) {
			if(this.dataConfig.getConfig().isConfigurationSection("kits")) {
				for(String key : this.dataConfig.getConfig().getConfigurationSection("kits").getKeys(false)) {
					if(!Practice.getInstance().getGameManager().gameTypeExists(key)) {
						this.dataConfig.getConfig().set("kits." + key, null);
						continue;
					}

					for(String innerKey : this.dataConfig.getConfig().getConfigurationSection("kits." + key).getKeys(false)) {
						if(!innerKey.chars().allMatch(Character::isDigit)) {
							this.dataConfig.getConfig().set("kits." + key + "." + innerKey, null);
							continue;
						}

						if(!this.dataConfig.getConfig().contains("kits." + key + "." + innerKey + ".inv")) {
							this.dataConfig.getConfig().set("kits." + key + "." + innerKey, null);
							this.dataConfig.save();
							this.player.sendMessage(ChatColor.RED + "One of your saved kits is corrupt and has been removed.");
							continue;
						}

						GameType gameType = Practice.getInstance().getGameManager().getGameType(key);
						PlayerKit kit = new PlayerKit(innerKey, InventoryUtil.playerInventoryFromString(this.dataConfig.getConfig().getString("kits." + key + "." + innerKey + ".inv")));
						this.gameKits.get(gameType).put(kit.getName(), kit);
					}
				}
				
				this.dataConfig.save();
			}
		}
	}

	public void saveAccount() {
		PracticeConfiguration.getStorage().saveAccount(this);
		
		this.dataConfig.getConfig().set("data.limited-matches", this.limitedMatches);
		this.dataConfig.getConfig().set("kits", null);
		
		for(Entry<GameType, Map<String, PlayerKit>> entry : this.gameKits.entrySet()) {
			this.dataConfig.getConfig().createSection(entry.getKey().getName());
			
			for(Entry<String, PlayerKit> kits : entry.getValue().entrySet()) {
				this.dataConfig.getConfig().set(kits.getKey() + ".inv", InventoryUtil.playerInventoryToString(kits.getValue().getInv()));
			}
		}
		
		this.dataConfig.save();
	}

}