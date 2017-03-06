package net.propvp.practice.command.type;

import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.propvp.practice.Practice;
import net.propvp.practice.command.SimpleCommand;
import net.propvp.practice.command.exception.CommandException;
import net.propvp.practice.command.exception.type.NeedPlayerException;
import net.propvp.practice.command.exception.type.TooFewArgumentsException;
import net.propvp.practice.game.GameType;
import net.propvp.practice.player.PlayerData;
import net.propvp.practice.player.PlayerElo;

public class EloCommand extends SimpleCommand {
	
	public EloCommand(Plugin plugin) {
		super(plugin);
	}
	
	public boolean requiresPermission() {
		return false;
	}

	public String getPermission() {
		return "";
	}

	public String[] getUsage() {
		return new String[] { "/<command>" };
	}

	public void onCommand(CommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof Player)) {
			throw new NeedPlayerException();
		}
		
		if(args.length == 0) {
			throw new TooFewArgumentsException();
		}
		
		Player player = (Player) sender;
		PlayerData data = Practice.getInstance().getDataManager().getData(player);

		player.sendMessage(ChatColor.GOLD + "Your Ratings");
		
		for(Entry<GameType, PlayerElo> entry : data.getRatings().entrySet()) {
			player.sendMessage(ChatColor.GRAY + " » " + ChatColor.YELLOW + entry.getKey().getName() + ChatColor.RED + " " + entry.getValue().getRating());
		}
	}
	
}