package net.propvp.practice.command.type;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.propvp.practice.Practice;
import net.propvp.practice.command.SimpleCommand;
import net.propvp.practice.command.exception.CommandException;
import net.propvp.practice.command.exception.type.*;
import net.propvp.practice.game.GameType;
import net.propvp.practice.player.PlayerInv;
import net.propvp.practice.utils.MessageUtils;

public class GameTypeCommand extends SimpleCommand {
	
	public GameTypeCommand(Plugin plugin) {
		super(plugin);
	}
	
	public boolean requiresPermission() {
		return true;
	}

	public String getPermission() {
		return "propractice.gametype";
	}

	public String[] getUsage() {
		return new String[] { "For help, use /<command> help." };
	}

	public void onCommand(CommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof Player)) throw new NeedPlayerException();
		if(args.length == 0) throw new TooFewArgumentsException();
		
		Player player = (Player) sender;
		
		if(args[0].equalsIgnoreCase("create")) {
			if(args.length < 2) throw new TooFewArgumentsException();
			if(Practice.getInstance().getGameManager().gameTypeExists(args[1])) throw new AlreadyExistsException();
			if(!args[1].chars().allMatch(Character::isLetter)) throw new IllegalCharacterException();
			
			GameType gameMode = new GameType(args[1]);
			Practice.getInstance().getGameManager().putGameType(args[1], gameMode);
			gameMode.save();
			MessageUtils.sendMessage(sender, String.format(ChatColor.GOLD + "Created game type " + ChatColor.YELLOW + "%s" + ChatColor.GOLD + ".", args[1]), new Object[0]);
		} else if(args[0].equalsIgnoreCase("delete")) {
			if(args.length < 2) throw new TooFewArgumentsException();
			if(!Practice.getInstance().getGameManager().gameTypeExists(args[1])) throw new DoesNotExistException();
			if(!args[1].chars().allMatch(Character::isLetter)) throw new IllegalCharacterException();

			Practice.getInstance().getGameManager().removeGameType(args[1]);
			MessageUtils.sendMessage(sender, String.format(ChatColor.GOLD + "Deleted game type " + ChatColor.YELLOW + "%s" + ChatColor.GOLD + ".", args[1]), new Object[0]);
		} else if(args[0].equalsIgnoreCase("setinventory")) {
			if(args.length < 2) throw new TooFewArgumentsException();
			if(!Practice.getInstance().getGameManager().gameTypeExists(args[1])) throw new DoesNotExistException();
			if(!args[1].chars().allMatch(Character::isLetter)) throw new IllegalCharacterException();
			
			GameType gameMode = Practice.getInstance().getGameManager().getGameType(args[1]);
			gameMode.setStartingInventory(PlayerInv.fromPlayerInventory(player.getInventory()));
			gameMode.save();
			MessageUtils.sendMessage(sender, String.format(ChatColor.GOLD + "Set the inventory for game type " + ChatColor.YELLOW + "%s" + ChatColor.GOLD + ".", args[1]), new Object[0]);
		} else if(args[0].equalsIgnoreCase("loadinventory")) {
			if(args.length < 2) throw new TooFewArgumentsException();
			if(!Practice.getInstance().getGameManager().gameTypeExists(args[1])) throw new DoesNotExistException();
			if(!args[1].chars().allMatch(Character::isLetter)) throw new IllegalCharacterException();
			
			GameType gameMode = Practice.getInstance().getGameManager().getGameType(args[1]);
			if(gameMode.getInventory() == null) throw new UnknownException("That game type does not have a default inventory setup.");
			
			player.getInventory().setContents(gameMode.getInventory().getContents());
			player.getInventory().setArmorContents(gameMode.getInventory().getArmorContents());
			player.updateInventory();
			MessageUtils.sendMessage(sender, String.format(ChatColor.GOLD + "Loaded the inventory for game type " + ChatColor.YELLOW + "%s" + ChatColor.GOLD + ".", args[1]), new Object[0]);
		} else if(args[0].equalsIgnoreCase("seticon")) {
			if(args.length < 2) throw new TooFewArgumentsException();
			if(!Practice.getInstance().getGameManager().gameTypeExists(args[1])) throw new DoesNotExistException();
			if(!args[1].chars().allMatch(Character::isLetter)) throw new IllegalCharacterException();
			if(player.getItemInHand() == null) throw new NeedItemException("You do not have an item in your hand.");
			
			GameType gameMode = Practice.getInstance().getGameManager().getGameType(args[1]);
			gameMode.setIcon(player.getItemInHand());
			gameMode.save();
			MessageUtils.sendMessage(sender, String.format(ChatColor.GOLD + "Set the display icon for game type " + ChatColor.YELLOW + "%s" + ChatColor.GOLD + ".", args[1]), new Object[0]);
		} else if(args[0].equalsIgnoreCase("setdisplayname")) {
			if(args.length < 3) throw new TooFewArgumentsException();
			if(!Practice.getInstance().getGameManager().gameTypeExists(args[1])) throw new DoesNotExistException();
			if(!args[1].chars().allMatch(Character::isLetter)) throw new IllegalCharacterException();
			
			GameType gameMode = Practice.getInstance().getGameManager().getGameType(args[1]);
			gameMode.setDisplayName(args[2]);
			gameMode.save();
			MessageUtils.sendMessage(sender, String.format(ChatColor.GOLD + "Set the display name for game type " + ChatColor.YELLOW + "%s" + ChatColor.GOLD + ".", args[1]), new Object[0]);
		} else if(args[0].equalsIgnoreCase("setmechanics")) {
			if(args.length < 2) throw new TooFewArgumentsException();
			if(!Practice.getInstance().getGameManager().gameTypeExists(args[1])) throw new DoesNotExistException();
			if(!args[1].chars().allMatch(Character::isLetter)) throw new IllegalCharacterException();

			GameType gameMode = Practice.getInstance().getGameManager().getGameType(args[1]);
			gameMode.openSettings(player);
			gameMode.save();
		} else if(args[0].equalsIgnoreCase("list")) {
			StringBuilder builder = new StringBuilder(ChatColor.GOLD + "Game Type(s): " + ChatColor.GRAY);
			Practice.getInstance().getGameManager().getGameTypes().keySet().forEach(name -> builder.append(name + ", "));
			player.sendMessage(builder.toString());
		} else if(args[0].equalsIgnoreCase("help")) {
			player.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
			player.sendMessage(ChatColor.AQUA + "/gt create [name]" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Create a game type.");
			player.sendMessage(ChatColor.AQUA + "/gt delete [name]" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Delete a game type.");
			player.sendMessage(ChatColor.AQUA + "/gt seticon [name]" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Set the display icon.");
			player.sendMessage(ChatColor.AQUA + "/gt setinventory [name]" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Set the default inventory.");
			player.sendMessage(ChatColor.AQUA + "/gt loadinventory [name]" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Loads the default inventory.");
			player.sendMessage(ChatColor.AQUA + "/gt setdisplayname [name] [text]" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Set the display name.");
			player.sendMessage(ChatColor.AQUA + "/gt setmechanics [name]" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Open the mechanics menu.");
			player.sendMessage(ChatColor.AQUA + "/gt list" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Show a list of all game types.");
			player.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
		} else {
			throw new InvalidUsageException();
		}
	}
	
}