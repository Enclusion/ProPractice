package net.propvp.practice.command.type;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.propvp.practice.Practice;
import net.propvp.practice.command.SimpleCommand;
import net.propvp.practice.command.exception.CommandException;
import net.propvp.practice.command.exception.type.*;
import net.propvp.practice.game.arena.Arena;
import net.propvp.practice.game.arena.Cuboid;
import net.propvp.practice.game.arena.CuboidSelection;
import net.propvp.practice.utils.ItemFactory;
import net.propvp.practice.utils.MessageUtils;

public class ArenaCommand extends SimpleCommand {

	public ArenaCommand(Plugin plugin) {
		super(plugin);
	}
	
	public boolean requiresPermission() {
		return true;
	}

	public String getPermission() {
		return "propractice.arena";
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
			if(Practice.getInstance().getArenaManager().arenaExists(args[1])) throw new AlreadyExistsException();
			if(!args[1].chars().allMatch(Character::isLetter)) throw new IllegalCharacterException();

			Arena arena = new Arena(args[1]);
			arena.save();
			Practice.getInstance().getArenaManager().putArena(args[1], arena);
			MessageUtils.sendMessage(sender, String.format(ChatColor.GOLD + "Created arena " + ChatColor.YELLOW + "%s" + ChatColor.GOLD + ".", args[1]), new Object[0]);
		} else if(args[0].equalsIgnoreCase("delete")) {
			if(args.length < 2) throw new TooFewArgumentsException();
			if(!Practice.getInstance().getArenaManager().arenaExists(args[1])) throw new DoesNotExistException("That arena does not exist.");
			if(!args[1].chars().allMatch(Character::isLetter)) throw new IllegalCharacterException();
			
			Practice.getInstance().getArenaManager().getArena(args[1]).remove();
			MessageUtils.sendMessage(sender, String.format(ChatColor.GOLD + "Deleted arena " + ChatColor.YELLOW + "%s" + ChatColor.GOLD + ".", args[1]), new Object[0]);
		} else if(args[0].equalsIgnoreCase("setspawn1")) {
			if(args.length < 2) throw new TooFewArgumentsException();
			if(!Practice.getInstance().getArenaManager().arenaExists(args[1])) throw new DoesNotExistException("That arena does not exist.");
			if(!args[1].chars().allMatch(Character::isLetter)) throw new IllegalCharacterException();
			
			Arena arena = Practice.getInstance().getArenaManager().getArena(args[1]);
			arena.setSpawn1(player.getLocation());
			arena.save();
			MessageUtils.sendMessage(sender, String.format(ChatColor.GOLD + "Updated spawn point 1 for arena " + ChatColor.YELLOW + "%s" + ChatColor.GOLD + ".", args[1]), new Object[0]);
		} else if(args[0].equalsIgnoreCase("setspawn2")) {
			if(args.length < 2) throw new TooFewArgumentsException();
			if(!Practice.getInstance().getArenaManager().arenaExists(args[1])) throw new DoesNotExistException("That arena does not exist.");
			if(!args[1].chars().allMatch(Character::isLetter)) throw new IllegalCharacterException();
			
			Arena arena = Practice.getInstance().getArenaManager().getArena(args[1]);
			arena.setSpawn2(player.getLocation());
			arena.save();
			MessageUtils.sendMessage(sender, String.format(ChatColor.GOLD + "Updated spawn point 2 for arena " + ChatColor.YELLOW + "%s" + ChatColor.GOLD + ".", args[1]), new Object[0]);
		} else if(args[0].equalsIgnoreCase("setregion")) {
			if(args.length < 2) throw new TooFewArgumentsException();
			if(!Practice.getInstance().getArenaManager().arenaExists(args[1])) throw new DoesNotExistException("That arena does not exist.");
			if(!args[1].chars().allMatch(Character::isLetter)) throw new IllegalCharacterException();
			if(!CuboidSelection.bothPointsSet(player)) throw new NeedItemException("You need to make a selection before setting a region.");
			
			Arena arena = Practice.getInstance().getArenaManager().getArena(args[1]);
			Cuboid cuboid = new Cuboid(CuboidSelection.getSelectionOne(player), CuboidSelection.getSelectionTwo(player));
			arena.setCuboid(cuboid);
			arena.save();
			MessageUtils.sendMessage(sender, String.format(ChatColor.GOLD + "Updated region for arena " + ChatColor.YELLOW + "%s" + ChatColor.GOLD + ".", args[1]), new Object[0]);
		} else if(args[0].equalsIgnoreCase("check")) {
			if(args.length < 2) throw new TooFewArgumentsException();
			if(!Practice.getInstance().getArenaManager().arenaExists(args[1])) throw new DoesNotExistException("That arena does not exist.");
			if(!args[1].chars().allMatch(Character::isLetter)) throw new IllegalCharacterException();
			
			Arena arena = Practice.getInstance().getArenaManager().getArena(args[1]);
			player.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
			player.sendMessage(ChatColor.YELLOW + " ** " + ChatColor.AQUA + arena.getName());
			player.sendMessage(ChatColor.YELLOW + " » " + ChatColor.AQUA + "Setup: " + (arena.isSetup() ? ChatColor.GREEN + "True" : ChatColor.RED + "False" ));
			player.sendMessage(ChatColor.YELLOW + " » " + ChatColor.AQUA + "Spawn1: " + (arena.getSpawn1() != null ? ChatColor.GREEN + "True" : ChatColor.RED + "False" ));
			player.sendMessage(ChatColor.YELLOW + " » " + ChatColor.AQUA + "Spawn2: " + (arena.getSpawn2() != null ? ChatColor.GREEN + "True" : ChatColor.RED + "False" ));
			player.sendMessage(ChatColor.YELLOW + " » " + ChatColor.AQUA + "Region: " + (arena.getCuboid() != null ? ChatColor.GREEN + "True" : ChatColor.RED + "False" ));
			player.sendMessage(ChatColor.YELLOW + " » " + ChatColor.AQUA + "In-Use: " + (arena.isActive() ? ChatColor.GREEN + "True" : ChatColor.RED + "False" ));
			player.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
		} else if(args[0].equalsIgnoreCase("wand")) {
			player.getInventory().addItem(ItemFactory.getRegionSelector());
			player.sendMessage(ChatColor.AQUA + "You've been given the region selector.");
		} else if(args[0].equalsIgnoreCase("list")) {
			StringBuilder builder = new StringBuilder(ChatColor.GOLD + "Arena(s): " + ChatColor.GRAY);
			Practice.getInstance().getArenaManager().getArenas().keySet().forEach(name -> builder.append(name + ", "));
			player.sendMessage(builder.toString());
		} else if(args[0].equalsIgnoreCase("help")) {
			player.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
			player.sendMessage(ChatColor.AQUA + "/arena create [name]" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Create an arena.");
			player.sendMessage(ChatColor.AQUA + "/arena delete [name]" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Delete an arena.");
			player.sendMessage(ChatColor.AQUA + "/arena setspawn1 [name]" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Set the spawn point 1.");
			player.sendMessage(ChatColor.AQUA + "/arena setspawn2 [name]" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Set the spawn point 2.");
			player.sendMessage(ChatColor.AQUA + "/arena setregion [name]" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Set the region.");
			player.sendMessage(ChatColor.AQUA + "/arena check [name]" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Show info on an arena.");
			player.sendMessage(ChatColor.AQUA + "/arena wand" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Spawn the region selector.");
			player.sendMessage(ChatColor.AQUA + "/arena list" + ChatColor.YELLOW + " » " + ChatColor.GRAY + "Show a list of all game types.");
			player.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
		} else {
			throw new InvalidUsageException();
		}
	}

}