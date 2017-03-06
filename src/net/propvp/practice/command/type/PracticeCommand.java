package net.propvp.practice.command.type;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.propvp.practice.PracticeConfiguration;
import net.propvp.practice.command.SimpleCommand;
import net.propvp.practice.command.exception.CommandException;
import net.propvp.practice.command.exception.type.InvalidUsageException;
import net.propvp.practice.command.exception.type.NeedPlayerException;
import net.propvp.practice.command.exception.type.TooFewArgumentsException;

public class PracticeCommand extends SimpleCommand {
	
	public PracticeCommand(Plugin plugin) {
		super(plugin);
	}
	
	public boolean requiresPermission() {
		return true;
	}

	public String getPermission() {
		return "propractice.admin";
	}

	public String[] getUsage() {
		return new String[] { "For help, use /<command> help." };
	}

	public void onCommand(CommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof Player)) throw new NeedPlayerException();
		if(args.length == 0) throw new TooFewArgumentsException();

		Player player = (Player) sender;
		
		if(args[0].equalsIgnoreCase("setspawn")) {
			PracticeConfiguration.setSpawnLocation(player.getLocation());
			PracticeConfiguration.saveLocations();
			player.sendMessage(ChatColor.GREEN + "The spawn point has been updated.");
		} else if(args[0].equalsIgnoreCase("seteditor")) {
			PracticeConfiguration.setEditorLocation(player.getLocation());
			PracticeConfiguration.saveLocations();
			player.sendMessage(ChatColor.GREEN + "The editor point has been updated.");
		} else if(args[0].equalsIgnoreCase("help")) {
			player.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
			player.sendMessage(ChatColor.AQUA + "/practice setspawn" + ChatColor.YELLOW + " >> " + ChatColor.GRAY + "Set the spawn point.");
			player.sendMessage(ChatColor.AQUA + "/practice seteditor" + ChatColor.YELLOW + " >> " + ChatColor.GRAY + "Set the editor point.");
			player.sendMessage(ChatColor.AQUA + "/arena help" + ChatColor.YELLOW + " >> " + ChatColor.GRAY + "Help for the arena command.");
			player.sendMessage(ChatColor.AQUA + "/gt help" + ChatColor.YELLOW + " >> " + ChatColor.GRAY + "Help for the game type command.");
			player.sendMessage(ChatColor.AQUA + "/ppv" + ChatColor.YELLOW + " >> " + ChatColor.GRAY + "ProPractice Version.");
			player.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
		} else {
			throw new InvalidUsageException();
		}
	}
	
}