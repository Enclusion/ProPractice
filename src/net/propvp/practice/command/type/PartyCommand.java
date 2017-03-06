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
import net.propvp.practice.event.party.*;
import net.propvp.practice.player.PlayerData;

public class PartyCommand extends SimpleCommand {
	
	public PartyCommand(Plugin plugin) {
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
		if (!(sender instanceof Player)) throw new NeedPlayerException();
		if(args.length == 0) throw new TooFewArgumentsException();
		
		Player player = (Player) sender;
		PlayerData data = Practice.getInstance().getDataManager().getData(player);
		
		if(args[0].equalsIgnoreCase("create")) {
			if(data.inParty()) throw new AlreadyExistsException("You already have a party.");

			PartyCreateEvent event = new PartyCreateEvent(player);
			Bukkit.getServer().getPluginManager().callEvent(event);
		} else if(args[0].equalsIgnoreCase("disband")) {
			if(!data.inParty()) throw new DoesNotExistException("You do not have a party.");
			if(data.getParty().getLeader() != player) throw new UnauthorizedActionException("You are not the leader of the your party.");
			
			PartyDisbandEvent event = new PartyDisbandEvent(player, data.getParty());
			Bukkit.getServer().getPluginManager().callEvent(event);
		} else if(args[0].equalsIgnoreCase("join")) {
			if(args.length < 2) throw new TooFewArgumentsException();
			if(data.inParty()) throw new AlreadyExistsException("You already have a party.");
			if(Bukkit.getPlayer(args[1]) == null) throw new PlayerOfflineException();
			if(!Practice.getInstance().getPartyManager().isParty(Bukkit.getPlayer(args[1]))) throw new DoesNotExistException("That party does not exist.");
			if(!Practice.getInstance().getPartyManager().getParty(Bukkit.getPlayer(args[1])).hasInvite(player)) throw new UnauthorizedActionException("You have not been invited to that party.");
			if(Practice.getInstance().getPartyManager().getParty(Bukkit.getPlayer(args[1])).getMembers().size() >= 8) throw new MaximumNumberException("That party has too many players.");
			
			PartyJoinEvent event = new PartyJoinEvent(player, data.getParty());
			Bukkit.getServer().getPluginManager().callEvent(event);
		} else if(args[0].equalsIgnoreCase("invite")) {
			if(args.length < 2) throw new TooFewArgumentsException();
			if(player.getName().equalsIgnoreCase(args[1])) throw new UnknownException("You cannot invite yourself to a party.");
			if(!data.inParty()) throw new DoesNotExistException("You do not have a party.");
			if(data.getParty().getLeader() != player) throw new UnauthorizedActionException("You are not the leader of the your party.");
			if(Bukkit.getPlayer(args[1]) == null) throw new PlayerOfflineException();
			
			PartyInviteEvent event = new PartyInviteEvent(Bukkit.getPlayer(args[1]), data.getParty());
			Bukkit.getServer().getPluginManager().callEvent(event);
		} else if(args[0].equalsIgnoreCase("kick")) {
			if(args.length < 2) throw new TooFewArgumentsException();
			if(!data.inParty()) throw new DoesNotExistException("You do not have a party.");
			if(data.getParty().getLeader() != player) throw new UnauthorizedActionException("You are not the leader of the your party.");
			if(player.getName().equalsIgnoreCase(args[1])) throw new UnknownException("You cannot kick yourself from your party.");
			if(Bukkit.getPlayer(args[1]) == null) throw new PlayerOfflineException();
			if(!data.getParty().getMembers().contains(Bukkit.getPlayer(args[1]))) throw new PlayerOfflineException();
			
			PartyKickEvent event = new PartyKickEvent(player, Bukkit.getPlayer(args[1]), data.getParty());
			Bukkit.getServer().getPluginManager().callEvent(event);
		} else if(args[0].equalsIgnoreCase("info")) {
			if(!data.inParty()) throw new DoesNotExistException("You do not have a party.");
			
			player.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
			player.sendMessage(ChatColor.AQUA + data.getParty().getLeader().getName() + "'s Party:");
			
			StringBuilder builder = new StringBuilder(ChatColor.YELLOW + " » " + ChatColor.GRAY + "Players: ");
			
			if(data.getParty().getMembers().isEmpty()) {
				builder.append("None");
			} else {
				for(Player p : data.getParty().getMembers()) {
					builder.append(p.getName() + ", ");
				}
			}
			
			player.sendMessage(builder.toString());
			
			player.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
		} else if(args[0].equalsIgnoreCase("help")) {
			player.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
			player.sendMessage(ChatColor.AQUA + "/party create" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Create a party.");
			player.sendMessage(ChatColor.AQUA + "/party join [player]" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Join a party.");
			player.sendMessage(ChatColor.AQUA + "/party disband" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Disband a party.");
			player.sendMessage(ChatColor.AQUA + "/party leave" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Leave a party.");
			player.sendMessage(ChatColor.AQUA + "/party kick [player]" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Kick a player.");
			player.sendMessage(ChatColor.AQUA + "/party invite [player]" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Invite a player.");
			player.sendMessage(ChatColor.AQUA + "/party info" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Show your party.");
			player.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
		} else {
			throw new InvalidUsageException();
		}
	}
	
}