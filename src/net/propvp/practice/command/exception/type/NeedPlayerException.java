package net.propvp.practice.command.exception.type;

import org.bukkit.ChatColor;

public class NeedPlayerException extends UsageException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return ChatColor.RED + "You did not provide a player.";
	}
	
}