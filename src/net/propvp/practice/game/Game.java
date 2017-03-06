package net.propvp.practice.game;

import java.util.List;

import org.bukkit.entity.Player;

import net.propvp.practice.timing.ElapsedTimer;

public interface Game {
	
	public GameType getGameType();
	
	public List<Player> getPlayers();
	
	public List<Player> getSpectators();
	
	public void addSpectator(Player player);

	public void removeSpectator(Player player);

	public Team getTeamOfPlayer(Player player);

	public Team getRival(Team team);

	public ElapsedTimer getTimer();

	public boolean hasStarted();
	
	public void cancelMatch(String reason);

}