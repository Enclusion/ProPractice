package net.propvp.practice.command.type;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.propvp.practice.command.SimpleCommand;
import net.propvp.practice.command.exception.CommandException;
import net.propvp.practice.command.exception.type.NeedPlayerException;
import net.propvp.practice.command.exception.type.TooFewArgumentsException;
import net.propvp.practice.utils.InventoryFactory;

public class InventoryCommand extends SimpleCommand {
	
	public InventoryCommand(Plugin plugin) {
		super(plugin);
	}
	
	public boolean requiresPermission() {
		return false;
	}

	public String getPermission() {
		return "propractice.gametype";
	}

	public String[] getUsage() {
		return new String[] { "/<command> [player]" };
	}

	public void onCommand(CommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof Player)) {
			throw new NeedPlayerException();
		}
		
		if(args.length == 0) {
			throw new TooFewArgumentsException();
		}
		
		if(!InventoryFactory.invs.containsKey(args[0])) {
			sender.sendMessage(ChatColor.RED + "Could not find that player's inventory.");
			return;
		}
		
		((Player)sender).openInventory(InventoryFactory.invs.get(args[0]));
		InventoryFactory.opened.add(((Player)sender).getUniqueId());
	}
	
}