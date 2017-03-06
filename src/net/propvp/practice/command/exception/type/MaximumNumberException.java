package net.propvp.practice.command.exception.type;

import org.bukkit.ChatColor;

public class MaximumNumberException extends UsageException {

	private static final long serialVersionUID = 1L;
	
	private String message;
	
	public MaximumNumberException() {
		message = ChatColor.RED + "The number given exceeds the number limit.";
	}
	
	public MaximumNumberException(String s) {
		message = ChatColor.RED + s;
	}

	@Override
	public String getMessage() {
		return message;
	}
	
}