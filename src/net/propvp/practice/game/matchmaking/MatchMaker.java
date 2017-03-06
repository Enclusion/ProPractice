package net.propvp.practice.game.matchmaking;

import java.util.List;
import java.util.UUID;

import net.propvp.practice.game.Game;
import net.propvp.practice.game.GameType;
import net.propvp.practice.timing.ElapsedTimer;

public interface MatchMaker {
	
	public String getName();
	
	public UUID getIdentifier();
	
	public GameType getGameType();
	
	public boolean isRanked();
	
	public boolean isParty();
	
	public boolean isFrozen();
	
	public void setFrozen(boolean val);
	
	public int getMinimumPlayers();
	
	public int getMaximumPlayers();
	
	public int getSearchingCount();
	
	public int getPlayingCount();
	
	public int getSearchRange(Object obj);
	
	public ElapsedTimer getSearchTimer(Object obj);
	
	public void addObject(Object obj);
	
	public void removeObject(Object obj);
	
	public boolean containsObject(Object obj);
	
	public List<Game> getMatches();
	
	public void removeMatch(Game game);

}