package net.propvp.practice.game;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import net.propvp.practice.party.Party;

public class Team {

	private String name;
	private Object object;
	private Map<Player, Boolean> players;
	
	public Team(Object object) {
		this.object = object;
		this.players = new HashMap<Player, Boolean>();
		
		if(object instanceof Player) {
			Player player = (Player) object;
			this.name = player.getName();
			this.players.put(player, true);
		} else if(object instanceof Party) {
			Party party = (Party) object;
			this.name = "team_" + party.getLeader().getName();
			this.players.put(party.getLeader(), true);
			
			for(Player p : party.getMembers()) {
				this.players.put(p, true);
			}
		}
	}
	
	public String getName() {
		return name;
	}
	
	public Object getObject() {
		return object;
	}
	
	public boolean isParty() {
		return (object instanceof Party);
	}
	
	public Map<Player, Boolean> getPlayers() {
		return players;
	}
	
	public int amountLeft() {
		return (int) players.entrySet().stream().filter(player -> player.getValue()).count();
	}
	
}