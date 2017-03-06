package net.propvp.practice.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.propvp.practice.Practice;
import net.propvp.practice.PracticeConfiguration;

public class MessageUtils {

	public static void sendMessage(CommandSender target, String fmt, Object... args) {
		String prefix = ChatColor.translateAlternateColorCodes('&', Practice.getInstance().getConfig().getString("chat.prefix", ""));
		target.sendMessage(prefix + String.format(fmt, (Object[]) args));
	}
	
	public static void sendMessageToStaff(String message) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.hasPermission("propractice.notifications")) {
				p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "!" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + message);
			}
		}
	}
	
	public static String getTranslation(String path) {
		return color(PracticeConfiguration.getMessageConfig().getConfig().getString(path));
	}
	
	public static String color(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	public static ChatColor getChatColorByCode(String colorCode) {
	    switch (colorCode) {
	        case "&b" : return ChatColor.AQUA;
	        case "&0" : return ChatColor.BLACK;
	        case "&9" : return ChatColor.BLUE;
	        case "&l" : return ChatColor.BOLD;
	        case "&3" : return ChatColor.DARK_AQUA;
	        case "&1" : return ChatColor.DARK_BLUE;
	        case "&8" : return ChatColor.DARK_GRAY;
	        case "&2" : return ChatColor.DARK_GREEN;
	        case "&5" : return ChatColor.DARK_PURPLE;
	        case "&4" : return ChatColor.DARK_RED;
	        case "&6" : return ChatColor.GOLD;
	        case "&7" : return ChatColor.GRAY;
	        case "&a" : return ChatColor.GREEN;
	        case "&o" : return ChatColor.ITALIC;
	        case "&d" : return ChatColor.LIGHT_PURPLE;
	        case "&k" : return ChatColor.MAGIC;
	        case "&c" : return ChatColor.RED;
	        case "&r" : return ChatColor.RESET;
	        case "&m" : return ChatColor.STRIKETHROUGH;
	        case "&n" : return ChatColor.UNDERLINE;
	        case "&f" : return ChatColor.WHITE;
	        case "&e" : return ChatColor.YELLOW;
	        default: return ChatColor.WHITE;
	    }
	}
	
}