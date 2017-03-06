package net.propvp.practice.event.party;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PartyCreateEvent extends Event implements Cancellable {
	
	private Player player;
	private boolean cancelled;
	private static final HandlerList handlers = new HandlerList();

	public PartyCreateEvent(Player player) {
		super();
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
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