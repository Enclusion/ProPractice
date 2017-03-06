package net.propvp.practice.command.type;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.propvp.practice.Practice;
import net.propvp.practice.command.SimpleCommand;
import net.propvp.practice.command.exception.CommandException;
import net.propvp.practice.command.exception.type.NeedPlayerException;
import net.propvp.practice.player.PlayerData;

public class ScoreboardCommand extends SimpleCommand {
	
	public ScoreboardCommand(Plugin plugin) {
		super(plugin);
	}
	
	public boolean requiresPermission() {
		return false;
	}

	public String getPermission() {
		return "propractice.gametype";
	}

	public String[] getUsage() {
		return new String[] { "/<command>" };
	}

	public void onCommand(CommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof Player)) {
			throw new NeedPlayerException();
		}
		
		Player player = (Player) sender;
		PlayerData data = Practice.getInstance().getDataManager().getData(player);
		
		if(data.isHidingScoreboard()) {
			player.sendMessage(ChatColor.LIGHT_PURPLE + "You are now seeing the scoreboard again.");
			data.setHidingScoreboard(false);
		} else {
			player.sendMessage(ChatColor.LIGHT_PURPLE + "You are now hiding the scoreboard.");
			data.setHidingScoreboard(true);
		}
	}
	
}