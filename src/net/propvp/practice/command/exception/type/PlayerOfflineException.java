package net.propvp.practice.command.exception.type;

import org.bukkit.ChatColor;

import net.propvp.practice.command.exception.CommandException;

public class PlayerOfflineException extends CommandException {

	private static final long serialVersionUID = 1L;
	
	@Override
	public String getMessage() {
		return ChatColor.RED + "That player is offline or cannot be found.";
	}
	
}
