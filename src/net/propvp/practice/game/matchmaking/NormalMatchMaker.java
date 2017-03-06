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
import net.propvp.practice.game.Game;
import net.propvp.practice.game.GameType;
import net.propvp.practice.game.Match;
import net.propvp.practice.game.arena.Arena;
import net.propvp.practice.party.Party;
import net.propvp.practice.player.PlayerData;
import net.propvp.practice.timing.ElapsedTimer;
import net.propvp.practice.utils.InventoryFactory;
import net.propvp.practice.utils.MessageUtils;

public class NormalMatchMaker implements MatchMaker {
	
	private String name;
	private UUID identifier;
	private GameType gameType;
	private boolean isRanked;
	private boolean isParty;
	private boolean isFrozen;
	private int maxPlayers;
	private int minPlayers;
	private List<Game> ongoingMatches;
	private LinkedList<Object> searchList;
	private Map<Object, Integer> searchRange;
	private Map<Object, ElapsedTimer> searchTimer;
	private BukkitTask searchTask;
	
	public NormalMatchMaker(GameType gameType, boolean isParty) {
		this.name = "Normal " + (isParty ? "2v2 " : "1v1 ") + gameType.getName();
		this.identifier = UUID.randomUUID();
		this.gameType = gameType;
		this.isRanked = false;
		this.isParty = isParty;
		this.isFrozen = false;
		this.maxPlayers = isParty ? 2 : 1;
		this.minPlayers = 1;
		this.ongoingMatches = new ArrayList<Game>();
		this.searchList = new LinkedList<Object>();
		this.searchRange = new HashMap<Object, Integer>();
		this.searchTimer = new HashMap<Object, ElapsedTimer>();
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

					Object object1 = searchList.poll();
					Object object2 = searchList.poll();

					searchRange.remove(object1);
					searchTimer.remove(object1);
					
					searchRange.remove(object2);
					searchTimer.remove(object2);
					
					if(isParty) {
						Party party1 = (Party) object1;
						party1.getMembers().forEach(player -> Practice.getInstance().getDataManager().getData(player).setMatchMaker(null));

						Party party2 = (Party) object2;
						party2.getMembers().forEach(player -> Practice.getInstance().getDataManager().getData(player).setMatchMaker(null));
					} else {
						Player player1 = (Player) object1;
						Practice.getInstance().getDataManager().getData(player1).setMatchMaker(null);
						
						Player player2 = (Player) object2;
						Practice.getInstance().getDataManager().getData(player2).setMatchMaker(null);
					}

					Match match = new Match(arena, gameType, NormalMatchMaker.this, object1, object2);
					NormalMatchMaker.this.ongoingMatches.add(match);
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
		if((obj instanceof Player) && isParty()) throw new RuntimeException("Incompatible objects attempted to be entered into matchmaking.");
		if((obj instanceof Party) && !isParty()) throw new RuntimeException("Incompatible objects attempted to be entered into matchmaking.");
		
		if(isParty) {
			Party party = (Party) obj;
			
			if(party.getMembers().size() < minPlayers) {
				party.sendMessage(MessageUtils.getTranslation("errors.party-too-few").replace("$minPlayer", minPlayers + ""));
				return;
			}
			
			if(party.getMembers().size() > maxPlayers) {
				party.sendMessage(MessageUtils.getTranslation("errors.party-too-many").replace("$maxPlayer", maxPlayers + ""));
				return;
			}
			
			boolean errorOccured = false;
			
			for(Player player : party.getMembers()) {
				PlayerData data = Practice.getInstance().getDataManager().getData(player);
				
				if(data.inMatch() || data.inMatchmaking() || data.isSpectating()) {
					errorOccured = true;
					party.sendMessage(MessageUtils.getTranslation("errors.party-busy-queue").replace("$player", player.getName()));
				}
			}
			
			if(!errorOccured) {
				party.getMembers().forEach(p -> {
					Practice.getInstance().getDataManager().getData(p).setMatchMaker(this);
					p.getInventory().setContents(InventoryFactory.getMemberInventory());
					p.getInventory().setArmorContents(InventoryFactory.getEmptyArmor());
					p.updateInventory();
				});
				
				Practice.getInstance().getDataManager().getData(party.getLeader()).setMatchMaker(this);
				party.getLeader().getInventory().setContents(InventoryFactory.getQueuedInventory());
				party.getLeader().getInventory().setArmorContents(InventoryFactory.getEmptyArmor());
				party.getLeader().updateInventory();
				
				searchList.offer(obj);
				searchTimer.put(obj, new ElapsedTimer());
				party.sendMessage(MessageUtils.getTranslation("queue.party-joined").replace("$queue", name));
			}
		} else {
			Player player = (Player) obj;
			PlayerData data = Practice.getInstance().getDataManager().getData(player);
			
			if(data.inMatch() || data.inMatchmaking() || data.isSpectating()) {
				player.sendMessage(MessageUtils.getTranslation("queue.player-busy-queue"));
				return;
			} else {
				data.setMatchMaker(this);
				player.getInventory().setContents(InventoryFactory.getQueuedInventory());
				player.getInventory().setArmorContents(InventoryFactory.getEmptyArmor());
				player.updateInventory();
				
				searchList.offer(obj);
				searchTimer.put(obj, new ElapsedTimer());
				player.sendMessage(MessageUtils.getTranslation("queue.player-joined").replace("$queue", name));
			}
		}
	}

	@Override
	public void removeObject(Object obj) {
		searchRange.remove(obj);
		searchList.remove(obj);
		searchTimer.remove(obj);
		
		if(isParty) {
			Party party = (Party) obj;
			party.sendMessage(MessageUtils.getTranslation("queue.party-removed"));
			party.getMembers().forEach(player -> Practice.getInstance().getDataManager().getData(player).setMatchMaker(null));
		} else {
			Player player = (Player) obj;
			Practice.getInstance().getDataManager().getData(player).setMatchMaker(null);
			player.sendMessage(MessageUtils.getTranslation("queue.player-removed"));
			player.getInventory().setContents(InventoryFactory.getDefaultInventory(player));
			player.getInventory().setArmorContents(InventoryFactory.getEmptyArmor());
			player.updateInventory();
		}
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