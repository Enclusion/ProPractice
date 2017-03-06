package net.propvp.practice.command.type;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.propvp.practice.Practice;
import net.propvp.practice.command.SimpleCommand;
import net.propvp.practice.command.exception.CommandException;
import net.propvp.practice.command.exception.type.*;
import net.propvp.practice.menu.DuelMenu;
import net.propvp.practice.player.PlayerData;

public class DuelCommand extends SimpleCommand {
	
	public DuelCommand(Plugin plugin) {
		super(plugin);
	}
	
	public boolean requiresPermission() {
		return false;
	}

	public String getPermission() {
		return "";
	}

	public String[] getUsage() {
		return new String[] { "For help, use /<command> help." };
	}

	public void onCommand(CommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof Player)) throw new NeedPlayerException();
		if(args.length == 0) throw new TooFewArgumentsException();
		
		Player player = (Player) sender;
		PlayerData data = Practice.getInstance().getDataManager().getData(player);
		
		if(args[0].toLowerCase().equalsIgnoreCase("accept")) {
			if(args.length < 2) throw new NeedPlayerException();
			
			Player target = Bukkit.getPlayer(args[1]);
			if(target == null) throw new PlayerOfflineException();
			if(!data.hasDuelInvite(target)) throw new DoesNotMatchException("You have not received an invite from that player.");

			data.getDuelInvite(target).receiverAccepts();
		} else if(args[0].toLowerCase().equalsIgnoreCase("decline")) {
			if(args.length < 2) throw new NeedPlayerException();
			
			Player target = Bukkit.getPlayer(args[1]);
			if(target == null) throw new PlayerOfflineException();
			if(!data.hasDuelInvite(target)) throw new DoesNotMatchException("You have not received an invite from that player.");
			
			target.sendMessage(ChatColor.RED + "Your duel request to " + player.getName() + " has been declined.");
			player.sendMessage(ChatColor.GREEN + "You have declined " + target.getName() + "'s duel request.");
			data.removeDuelInvite(target);
		} else if(args[0].toLowerCase().equalsIgnoreCase("requests")) {
			if(data.isHidingRequests()) {
				data.setHidingRequests(false);
				player.sendMessage(ChatColor.LIGHT_PURPLE + "You are no longer hiding requests.");
			} else {
				data.setHidingRequests(true);
				player.sendMessage(ChatColor.LIGHT_PURPLE + "You are longer hiding requests.");
			}
		} else if(args[0].toLowerCase().equalsIgnoreCase("help")) {
			player.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
			player.sendMessage(ChatColor.AQUA + "/duel [player]" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Invite a player to a duel.");
			player.sendMessage(ChatColor.AQUA + "/duel accept [player]" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Accepts a duel from a player.");
			player.sendMessage(ChatColor.AQUA + "/duel decline [player]" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Declines a duel from a player.");
			player.sendMessage(ChatColor.AQUA + "/duel requests" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Toggles your duel requests.");
			player.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
		} else {
			if(args.length < 2) {
				if(player.getName().equalsIgnoreCase(args[0])) throw new UnknownException("You cannot invite yourself to a duel.");
				Player target = Bukkit.getPlayer(args[0]);
				if(target == null) throw new PlayerOfflineException();
				PlayerData targetData = Practice.getInstance().getDataManager().getData(target);
				
				if(data.inParty() != targetData.inParty()) {
					player.sendMessage(ChatColor.RED + (data.inParty() ? "You can't invite someone to a duel if you're in a party and they're not." : "You can't invite a player in a party to a duel if you're not in a party."));
					return;
				}
				
				DuelMenu.init(player, target);
			}
		}
	}
	
}