package net.propvp.practice.event.party;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.propvp.practice.party.Party;

public class PartyLeaveEvent extends Event implements Cancellable {
	
	private Player player;
	private Party party;
	private boolean cancelled;
	private static final HandlerList handlers = new HandlerList();

	public PartyLeaveEvent(Player player, Party party) {
		super();
		this.player = player;
		this.party = party;
	}
	
	public Player getPlayer() {
		return player;
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