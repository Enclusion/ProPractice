package net.propvp.practice.command.type;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.propvp.practice.Practice;
import net.propvp.practice.command.SimpleCommand;
import net.propvp.practice.command.exception.CommandException;
import net.propvp.practice.command.exception.type.NeedPlayerException;

public class VersionCommand extends SimpleCommand {
	
	public VersionCommand(Plugin plugin) {
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
		if (!(sender instanceof Player)) {
			throw new NeedPlayerException();
		}
		
		Player player = (Player) sender;
		
		player.sendMessage(ChatColor.DARK_GRAY + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
		player.sendMessage(ChatColor.DARK_GRAY + "\u2588" + ChatColor.DARK_RED + "\u2588\u2588\u2588" + ChatColor.DARK_GRAY + "\u2588" + ChatColor.DARK_RED + "\u2588\u2588\u2588" + ChatColor.DARK_GRAY + "\u2588");
		player.sendMessage(ChatColor.DARK_GRAY + "\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.DARK_GRAY + "\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.DARK_GRAY + "\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.DARK_GRAY + "\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.DARK_GRAY + "\u2588   ProPractice Version " + Practice.getInstance().getDescription().getVersion());
		player.sendMessage(ChatColor.DARK_GRAY + "\u2588" + ChatColor.DARK_RED + "\u2588\u2588\u2588" + ChatColor.DARK_GRAY + "\u2588" + ChatColor.DARK_RED + "\u2588\u2588\u2588" + ChatColor.DARK_GRAY + "\u2588   Made by deaL_ / joeleoli");
		player.sendMessage(ChatColor.DARK_GRAY + "\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.DARK_GRAY + "\u2588\u2588\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.DARK_GRAY + "\u2588\u2588\u2588   In-game: joeleoli");
		player.sendMessage(ChatColor.DARK_GRAY + "\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.DARK_GRAY + "\u2588\u2588\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.DARK_GRAY + "\u2588\u2588\u2588");
		player.sendMessage(ChatColor.DARK_GRAY + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
	}
	
}