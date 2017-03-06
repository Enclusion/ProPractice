package net.propvp.practice.event.party;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.propvp.practice.party.Party;

public class PartyKickEvent extends Event implements Cancellable {
	
	private Player initiator;
	private Player kicked;
	private Party party;
	private boolean cancelled;
	private static final HandlerList handlers = new HandlerList();

	public PartyKickEvent(Player initiator, Player kicked, Party party) {
		super();
		this.initiator = initiator;
		this.kicked = kicked;
		this.party = party;
	}
	
	public Player getInitiator() {
		return initiator;
	}
	
	public Player getKicked() {
		return kicked;
	}
	
	public Party getParty() {
		return party;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean bln) {
		this.cancelled = bln;
	}

	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}