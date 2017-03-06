package net.propvp.practice.party;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class PartyManager {

	private Map<Player, Party> parties;
	
	public PartyManager() {
		parties = new HashMap<Player, Party>();
	}
	
	public void addSet(Player player, Party party) {
		parties.put(player, party);
	}
	
	public void removeSet(Player key) {
		parties.remove(key);
	}
	
	public boolean isParty(Player key) {
		return (parties.containsKey(key)) ? true : false;
	}
	
	public Party getParty(Player key) {
		return parties.get(key);
	}
	
}