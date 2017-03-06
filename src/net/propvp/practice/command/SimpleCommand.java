package net.propvp.practice.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import net.propvp.practice.command.exception.CommandException;
import net.propvp.practice.command.exception.type.NoPermissionException;
import net.propvp.practice.command.exception.type.UsageException;
import net.propvp.practice.utils.MessageUtils;

public abstract class SimpleCommand implements CommandExecutor {
	
	protected Plugin plugin;

	public SimpleCommand(Plugin plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (this.requiresPermission()) {
				if (!sender.hasPermission(this.getPermission())) {
					throw new NoPermissionException();
				}
			}
			
			this.onCommand(sender, args);
		} catch (UsageException e) {
			MessageUtils.sendMessage(sender, e.getMessage(), new Object[0]);

			for (String s : this.getUsage()) {
				MessageUtils.sendMessage(sender, String.format(ChatColor.RED + "%s", s.replace("<command>", label)), new Object[0]);
			}
		} catch(CommandException e2) {
			MessageUtils.sendMessage(sender, e2.getMessage(), new Object[0]);
		}
		
		return true;
	}
	
	public abstract boolean requiresPermission();

	public abstract String getPermission();

	public abstract String[] getUsage();

	protected abstract void onCommand( CommandSender p0,  String[] p1) throws CommandException;
	
}