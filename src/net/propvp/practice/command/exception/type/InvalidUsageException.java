package net.propvp.practice.command.exception.type;

import org.bukkit.ChatColor;

public class InvalidUsageException extends UsageException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return ChatColor.RED + "Invalid syntax for that command.";
	}
	
}