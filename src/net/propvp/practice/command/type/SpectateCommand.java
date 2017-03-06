package net.propvp.practice.command.type;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import net.propvp.practice.command.SimpleCommand;
import net.propvp.practice.command.exception.CommandException;
import net.propvp.practice.command.exception.type.NeedPlayerException;
import net.propvp.practice.command.exception.type.TooFewArgumentsException;

public class SpectateCommand extends SimpleCommand {
	
	public SpectateCommand(Plugin plugin) {
		super(plugin);
	}
	
	public boolean requiresPermission() {
		return false;
	}

	public String getPermission() {
		return "";
	}

	public String[] getUsage() {
		return new String[] { "/<command> [player]" };
	}

	public void onCommand(CommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof Player)) throw new NeedPlayerException();
		if(args.length == 0) throw new TooFewArgumentsException();

		sender.sendMessage(ChatColor.RED + "This feature has currently been disabled.");
	}
	
}