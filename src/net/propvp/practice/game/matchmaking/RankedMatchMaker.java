package net.propvp.practice.game.matchmaking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.propvp.practice.Practice;
import net.propvp.practice.PracticeConfiguration;
import net.propvp.practice.game.Game;
import net.propvp.practice.game.GameType;
import net.propvp.practice.game.Match;
import net.propvp.practice.game.arena.Arena;
import net.propvp.practice.player.PlayerData;
import net.propvp.practice.timing.ElapsedTimer;
import net.propvp.practice.utils.InventoryFactory;
import net.propvp.practice.utils.MessageUtils;

public class RankedMatchMaker implements MatchMaker {

	private String name;
	private UUID identifier;
	private GameType gameType;
	private boolean isRanked;
	private boolean isParty;
	private boolean isFrozen;
	private int maxPlayers;
	private int minPlayers;
	private List<Game> ongoingMatches;
	private LinkedList<Player> searchList;
	private Map<Player, Integer> searchRange;
	private Map<Player, ElapsedTimer> searchTimer;
	private BukkitTask searchTask;

	public RankedMatchMaker(GameType gameType, boolean isRanked) {
		this.name = "Ranked 1v1 " + gameType.getName();
		this.identifier = UUID.randomUUID();
		this.gameType = gameType;
		this.isRanked = isRanked;
		this.isParty = false;
		this.isFrozen = false;
		this.maxPlayers = 1;
		this.minPlayers = 1;
		this.ongoingMatches = new ArrayList<Game>();
		this.searchList = new LinkedList<Player>();
		this.searchRange = new HashMap<Player, Integer>();
		this.searchTimer = new HashMap<Player, ElapsedTimer>();
		this.startSearchTask();
	}

	public String getName() {
		return name;
	}

	public UUID getIdentifier() {
		return identifier;
	}

	public GameType getGameType() {
		return gameType;
	}

	public boolean isRanked() {
		return isRanked;
	}

	public boolean isParty() {
		return isParty;
	}

	public boolean isFrozen() {
		return isFrozen;
	}

	public void setFrozen(boolean val) {
		this.isFrozen = val;
	}

	public int getMinimumPlayers() {
		return minPlayers;
	}

	public int getMaximumPlayers() {
		return maxPlayers;
	}

	public boolean containsObject(Object obj) {
		return searchList.contains(obj);
	}

	public int getSearchRange(Object obj) {
		return searchRange.get(obj);
	}

	public ElapsedTimer getSearchTimer(Object obj) {
		return searchTimer.get(obj);
	}

	public void startSearchTask() {
		this.searchTask = new BukkitRunnable() {
			public void run() {
				if(searchList.size() >= 2 && Practice.getInstance().getArenaManager().getUnusedArena() != null) {
					Object[] firstPair = searchList.toArray();

					if(firstPair[0] == null) { removeObject(firstPair[0]); return; }
					if(firstPair[1] == null) { removeObject(firstPair[1]); return; }

					Arena arena = Practice.getInstance().getArenaManager().getUnusedArena();
					arena.setActive(true);

					Player player1 = searchList.poll();
					Player player2 = searchList.poll();

					Practice.getInstance().getDataManager().getData(player1).setMatchMaker(null);
					Practice.getInstance().getDataManager().getData(player2).setMatchMaker(null);

					searchRange.remove(player1);
					searchRange.remove(player2);
					searchTimer.remove(player1);
					searchTimer.remove(player2);

					Match match = new Match(arena, gameType, RankedMatchMaker.this, player1, player2);
					RankedMatchMaker.this.ongoingMatches.add(match);
				}
			}
		}.runTaskTimer(Practice.getInstance(), 0L, 10L);
	}

	public void stopSearchTask() {
		this.searchTask.cancel();
	}

	@Override
	public int getSearchingCount() {
		return searchList.size();
	}

	@Override
	public int getPlayingCount() {
		if(ongoingMatches.isEmpty()) return 0;
		int i = 0;

		for(Game game : ongoingMatches) {
			i = i + game.getPlayers().size();
		}

		return i;
	}

	@Override
	public void addObject(Object obj) {
		if(!(obj instanceof Player)) throw new RuntimeException("Incompatible objects attempted to be entered into matchmaking.");

		Player player = (Player) obj;
		PlayerData data = Practice.getInstance().getDataManager().getData(player);

		if(PracticeConfiguration.isLimitedMatches() && !player.hasPermission("practice.limited-matches.bypass")) {
			if(data.getLimitedMatchesRemaining() <= 0) {
				player.sendMessage(MessageUtils.getTranslation("limited-matches.match.match-limit-reached"));
			} else {
				player.sendMessage(MessageUtils.getTranslation("limited-matches.match-limit-notification").replace("$amountRemaining", data.getLimitedMatchesRemaining() + ""));
			}
			
			return;
		}

		if(data.inMatchmaking() || data.inMatch() || data.isSpectating() || data.inParty()) {
			player.sendMessage(MessageUtils.getTranslation("queue.player-busy-queue"));
			return;
		}

		data.setMatchMaker(this);
		searchList.offer((Player)obj);
		searchTimer.put(player, new ElapsedTimer());
		searchRange.put(player, 200);

		player.sendMessage(MessageUtils.getTranslation("queue.player-joined-ranked").replace("$queue", name).replace("$playerElo", String.valueOf(data.getRating(gameType).getRating())));

		new BukkitRunnable() {
			public void run() {
				if(!RankedMatchMaker.this.searchRange.containsKey(player)) {
					this.cancel();
					return;
				}

				int i = RankedMatchMaker.this.searchRange.get(player);

				if(i >= 500) {
					player.sendMessage(MessageUtils.getTranslation("queue.time-limit-reached"));
					this.cancel();
					return;
				}

				RankedMatchMaker.this.searchRange.replace(player, i + 50);
				player.sendMessage(MessageUtils.getTranslation("queue.search-ranges").replace("$ranges", String.valueOf("" + (data.getRating(gameType).getRating() - RankedMatchMaker.this.searchRange.get(player)) + " -> " + (data.getRating(gameType).getRating() + RankedMatchMaker.this.searchRange.get(player)))));
			}
		}.runTaskTimer(Practice.getInstance(), 0L, 100L);

		player.getInventory().setContents(InventoryFactory.getQueuedInventory());
		player.getInventory().setArmorContents(InventoryFactory.getEmptyArmor());
		player.updateInventory();
	}

	@Override
	public void removeObject(Object obj) {
		searchRange.remove(obj);
		searchList.remove(obj);
		searchTimer.remove(obj);

		Player player = (Player) obj;
		player.sendMessage(MessageUtils.getTranslation("queue.player-removed"));
		player.getInventory().setContents(InventoryFactory.getDefaultInventory(player));
		player.getInventory().setArmorContents(InventoryFactory.getEmptyArmor());
		player.updateInventory();
		Practice.getInstance().getDataManager().getData(player).setMatchMaker(null);
	}
	
	@Override
	public List<Game> getMatches() {
		return this.ongoingMatches;
	}

	@Override
	public void removeMatch(Game game) {
		this.ongoingMatches.remove(game);
	}

}