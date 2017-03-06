package net.propvp.practice.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import mkremins.fanciful.FancyMessage;
import net.propvp.practice.Practice;
import net.propvp.practice.event.party.PartyCreateEvent;
import net.propvp.practice.event.party.PartyDisbandEvent;
import net.propvp.practice.event.party.PartyInviteEvent;
import net.propvp.practice.event.party.PartyJoinEvent;
import net.propvp.practice.event.party.PartyKickEvent;
import net.propvp.practice.event.party.PartyLeaveEvent;
import net.propvp.practice.party.Party;
import net.propvp.practice.player.PlayerData;
import net.propvp.practice.utils.InventoryFactory;

public class PartyListener implements Listener {

	private Practice plugin;

	public PartyListener(Practice plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PlayerData data = plugin.getDataManager().getData(player);
		
		if(data.inParty()) {
			if(data.getParty() == null) return;
			if(data.getParty().getLeader() == player) {
				PartyDisbandEvent call = new PartyDisbandEvent(player, data.getParty());
				Bukkit.getServer().getPluginManager().callEvent(call);
			} else {
				PartyLeaveEvent call = new PartyLeaveEvent(event.getPlayer(), data.getParty());
				Bukkit.getServer().getPluginManager().callEvent(call);
			}
		}
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		PlayerData data = plugin.getDataManager().getData(player);
		
		if(data.inParty()) {
			if(data.getParty().getLeader() == player) {
				PartyDisbandEvent call = new PartyDisbandEvent(player, data.getParty());
				Bukkit.getServer().getPluginManager().callEvent(call);
			} else {
				PartyLeaveEvent call = new PartyLeaveEvent(event.getPlayer(), data.getParty());
				Bukkit.getServer().getPluginManager().callEvent(call);
			}
		}
	}
	
	@EventHandler
	public void onPartyCreate(PartyCreateEvent event) {
		event.getPlayer().sendMessage(ChatColor.GRAY + "You have created a party.");
		event.getPlayer().getInventory().setContents(InventoryFactory.getLeaderInventory());
		event.getPlayer().updateInventory();
		
		Party party = new Party(event.getPlayer());
		Practice.getInstance().getDataManager().getData(event.getPlayer()).setParty(party);
		Practice.getInstance().getPartyManager().addSet(event.getPlayer(), party);
	}
	
	@EventHandler
	public void onPartyInvite(PartyInviteEvent event) {
		event.getParty().addInvite(event.getPlayer());
		event.getParty().sendMessage("" + ChatColor.LIGHT_PURPLE + event.getPlayer().getName() + ChatColor.GRAY + " has been invited to join the party.");
		event.getPlayer().sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "Party" + ChatColor.GRAY + "] " + ChatColor.GRAY + "You've been invited to join " + ChatColor.LIGHT_PURPLE + event.getParty().getLeader().getName() + ChatColor.GRAY + "'s party.");
		new FancyMessage("[").color(ChatColor.GRAY).then("Party").color(ChatColor.DARK_PURPLE).then("] ").color(ChatColor.GRAY).then("Click HERE to join the party.").command("/party join " + event.getParty().getLeader().getName()).color(ChatColor.GREEN).send(event.getPlayer());
	}
	
	@EventHandler
	public void onPartyJoin(PartyJoinEvent event) {
		if(event.getParty() == null) {
			event.getPlayer().sendMessage(ChatColor.RED + "You were being added to a party but the party appears to be missing.");
			return;
		}
		
		plugin.getDataManager().getData(event.getPlayer()).setParty(event.getParty());
		event.getPlayer().getInventory().setContents(InventoryFactory.getMemberInventory());
		event.getPlayer().updateInventory();
		event.getParty().addMember(event.getPlayer());
		event.getParty().removeInvite(event.getPlayer());
		event.getParty().sendMessage(ChatColor.LIGHT_PURPLE + event.getPlayer().getName() + ChatColor.GRAY + " has joined the party.");
	}
	
	@EventHandler
	public void onPartyLeave(PartyLeaveEvent event) {
		plugin.getDataManager().getData(event.getPlayer()).setParty(null);
		event.getPlayer().getInventory().setContents(InventoryFactory.getDefaultInventory(event.getPlayer()));
		event.getPlayer().updateInventory();
		event.getParty().removeMember(event.getPlayer());
		event.getParty().sendMessage("" + ChatColor.LIGHT_PURPLE + event.getPlayer().getName() + ChatColor.GRAY + " has left the party.");
	}
	
	@EventHandler
	public void onPartyKick(PartyKickEvent event) {
		plugin.getDataManager().getData(event.getKicked()).setParty(null);
		event.getKicked().getInventory().setContents(InventoryFactory.getDefaultInventory(event.getKicked()));
		event.getKicked().updateInventory();
		event.getParty().removeMember(event.getKicked());
		event.getParty().sendMessage("" + ChatColor.LIGHT_PURPLE + event.getKicked().getName() + ChatColor.GRAY + " has been kicked from the party by " + ChatColor.LIGHT_PURPLE + event.getParty().getLeader().getName() + ChatColor.GRAY + ".");
	}
	
	@EventHandler
	public void onPartyDisband(PartyDisbandEvent event) {
		for(Player player : event.getParty().getMembers()) {
			Practice.getInstance().getDataManager().getData(player).setParty(null);
			player.getInventory().setContents(InventoryFactory.getDefaultInventory(player));
			player.updateInventory();
		}
		
		plugin.getDataManager().getData(event.getPlayer()).setParty(null);
		event.getPlayer().getInventory().setContents(InventoryFactory.getDefaultInventory(event.getPlayer()));
		event.getPlayer().updateInventory();
		event.getParty().sendMessage("" + ChatColor.LIGHT_PURPLE + event.getParty().getLeader().getName() + ChatColor.GRAY + " has disbanded the party.");
		event.getParty().disband();
		event.getParty().clean();
	}
	
}