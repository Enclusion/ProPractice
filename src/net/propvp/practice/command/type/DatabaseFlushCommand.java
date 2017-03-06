package net.propvp.practice.command.type;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.propvp.practice.PracticeConfiguration;
import net.propvp.practice.command.SimpleCommand;
import net.propvp.practice.command.exception.CommandException;
import net.propvp.practice.command.exception.type.*;

public class DatabaseFlushCommand extends SimpleCommand {

	public DatabaseFlushCommand(Plugin plugin) {
		super(plugin);
	}
	
	public boolean requiresPermission() {
		return true;
	}

	public String getPermission() {
		return "propractice.admin";
	}

	public String[] getUsage() {
		return new String[] { "There are no parameters for that command. Just use /<command>." };
	}

	public void onCommand(CommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof Player)) throw new NeedPlayerException();
		Player player = (Player) sender;

		if(args.length == 0) {
			player.sendMessage(ChatColor.DARK_RED + "CONFIRMATION:" + ChatColor.RED + " Type '/dbflush confirm' to flush the database.");
			return;
		}

		if(args[0].equalsIgnoreCase("confirm")) {
			player.sendMessage(ChatColor.GREEN + "Flushing database...");

			try {
				PracticeConfiguration.getStorage().flushData();
			} catch(Exception e) {
				throw new UnknownException("Failed to flush database...");
			}

			player.sendMessage(ChatColor.GREEN + "Flushed database...");
			player.sendMessage(ChatColor.GREEN + "Reload the server for automatic backend configuration.");
		} else {
			throw new InvalidUsageException();
		}
	}

}