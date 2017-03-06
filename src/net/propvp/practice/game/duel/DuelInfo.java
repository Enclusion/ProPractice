package net.propvp.practice.game.duel;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import mkremins.fanciful.FancyMessage;
import net.propvp.practice.Practice;
import net.propvp.practice.game.GameType;
import net.propvp.practice.game.Match;
import net.propvp.practice.game.arena.Arena;
import net.propvp.practice.player.PlayerData;

public class DuelInfo {

	private Player sender;
	private Player receiver;
	private GameType gameType;
	private boolean isParty;
	
	public DuelInfo(Player sender, Player receiver, GameType gameType, boolean isParty) {
		this.sender = sender;
		this.receiver = receiver;
		this.gameType = gameType;
		this.isParty = isParty;
		
		this.sender.sendMessage(ChatColor.LIGHT_PURPLE + "DUEL " + ChatColor.GRAY + "Sent duel request to " + this.receiver.getName() + ".");
		new FancyMessage("DUEL ").color(ChatColor.LIGHT_PURPLE).then(this.sender.getName() + " has invited you to a duel (" + gameType.getName() + ").").color(ChatColor.GRAY).then(" ACCEPT").color(ChatColor.GREEN).command("/duel accept " + this.sender.getName()).then(" OR ").color(ChatColor.GRAY).then("DECLINE").color(ChatColor.RED).command("/duel decline " + this.sender.getName()).send(this.receiver);
	}
	
	public Player getSender() {
		return sender;
	}
	
	public Player getReceiver() {
		return receiver;
	}
	
	public GameType getGameType() {
		return gameType;
	}
	
	public boolean isParty() {
		return isParty;
	}
	
	public void receiverAccepts() {
		PlayerData senderData = Practice.getInstance().getDataManager().getData(this.sender);
		PlayerData receiverData = Practice.getInstance().getDataManager().getData(this.receiver);

		receiverData.removeDuelInvite(this.sender);
		
		this.sender.sendMessage(ChatColor.RED + "Your duel request to " + this.receiver.getName() + " has been accepted.");
		this.receiver.sendMessage(ChatColor.GREEN + "You have accepted " + this.sender.getName() + "'s duel request.");
		
		Arena arena = Practice.getInstance().getArenaManager().getUnusedArena();
		
		if(arena == null) {
			this.sender.sendMessage(ChatColor.LIGHT_PURPLE + "DUEL " + ChatColor.GRAY + "Attempted to start a duel with you but there are no available arenas.");
			this.receiver.sendMessage(ChatColor.LIGHT_PURPLE + "DUEL " + ChatColor.GRAY + "Attempted to start a duel with you but there are no available arenas.");
			return;
		}

		Match match = new Match(arena, this.gameType, null, this.sender, this.receiver);
		
		senderData.setMatch(match);
		receiverData.setMatch(match);
	}
	
	public void receiverDeclines() {
		
	}
	
	public void destroy() {
		this.sender = null;
		this.receiver = null;
		this.gameType = null;
	}

}