package net.propvp.practice.command.type;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.propvp.practice.command.SimpleCommand;
import net.propvp.practice.command.exception.CommandException;
import net.propvp.practice.command.exception.type.NeedPlayerException;
import net.propvp.practice.utils.PingUtil;

public class PingCommand extends SimpleCommand {
	
	public PingCommand(Plugin plugin) {
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
		if(!(sender instanceof Player)) throw new NeedPlayerException();

		Player player = (Player) sender;
		
		if(args.length == 0) {
			player.sendMessage(ChatColor.GOLD + "Your Ping: " + ChatColor.GRAY + PingUtil.getPing(player));
		} else {
			if(Bukkit.getPlayer(args[0]) == null) {
				player.sendMessage(ChatColor.RED + "That player is not online.");
			} else {
				Player targetPlayer = Bukkit.getPlayer(args[0]);
				player.sendMessage(ChatColor.GOLD + targetPlayer.getName() + "'s Ping: " + ChatColor.GRAY + PingUtil.getPing(targetPlayer));
			}
		}
	}
	
}