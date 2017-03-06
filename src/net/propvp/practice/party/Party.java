package net.propvp.practice.party;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import net.propvp.practice.Practice;

public class Party implements Listener {

	private Player leader;
	private List<Player> members;
	private List<UUID> invites;
	private String prefix;
	
	public Party(Player leader) {
		this.leader = leader;
		this.members = new ArrayList<Player>();
		this.invites = new ArrayList<UUID>();
		this.prefix = ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "Party" + ChatColor.GRAY + "] ";
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	public Player getLeader() {
		return leader;
	}
	
	public List<Player> getMembers() {
		return members;
	}
	
	public void addInvite(Player player) {
		if(this.invites.contains(player.getUniqueId())) return;
		
		this.invites.add(player.getUniqueId());
	}
	
	public void removeInvite(Player player) {
		if(!this.invites.contains(player)) return;
		
		this.invites.remove(player.getUniqueId());
	}
	
	public boolean hasInvite(Player player) {
		return (this.invites.contains(player.getUniqueId()));
	}
	
	public void addMember(Player player) {
		if(this.members.contains(player)) return;
		
		this.members.add(player);
	}
	
	public void removeMember(Player player) {
		if(!this.members.contains(player)) return;
		
		this.members.remove(player);
	}
	
	public void disband() {
		this.members.clear();
		this.invites.clear();
		this.members = null;
		this.invites = null;
		this.leader = null;
	}
	
	public void clean() {
		HandlerList.unregisterAll(this);
	}
	
	public void sendMessage(String message) {
		leader.sendMessage(prefix + ChatColor.GRAY + message);
		
		if(members != null) {
			for(Player p : members) {
				p.sendMessage(prefix + ChatColor.GRAY + message);
			}
		}
	}

	
}