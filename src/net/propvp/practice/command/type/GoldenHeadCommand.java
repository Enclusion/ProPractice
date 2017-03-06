package net.propvp.practice.command.type;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.propvp.practice.command.SimpleCommand;
import net.propvp.practice.command.exception.CommandException;
import net.propvp.practice.command.exception.type.*;
import net.propvp.practice.utils.ItemBuilder;

public class GoldenHeadCommand extends SimpleCommand {
	
	public GoldenHeadCommand(Plugin plugin) {
		super(plugin);
	}
	
	public boolean requiresPermission() {
		return true;
	}

	public String getPermission() {
		return "propractice.admin";
	}

	public String[] getUsage() {
		return new String[] { "/<command> [amount]" };
	}

	public void onCommand(CommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof Player)) throw new NeedPlayerException();
		if(args.length == 0) throw new TooFewArgumentsException("You did not provide an amount of golden heads.");
		if(!args[0].chars().allMatch(Character::isDigit)) throw new IllegalCharacterException();
		if(Integer.parseInt(args[0]) > 64) throw new MaximumNumberException("You cannot give yourself more than 64 golden heads.");
		
		Player player = (Player) sender;
		player.getInventory().addItem(new ItemBuilder(Material.GOLDEN_APPLE, ChatColor.GOLD + "Golden Head", Integer.parseInt(args[0]), ChatColor.GRAY + "Eat this to regenerate health.").getItem());
		player.sendMessage(ChatColor.GREEN + "You have been given " + Integer.parseInt(args[0]) + " golden heads.");
	}
	
}