package net.propvp.practice.command.exception.type;

import org.bukkit.ChatColor;

public class UnauthorizedActionException extends UsageException {

	private static final long serialVersionUID = 1L;
	
	private String message;
	
	public UnauthorizedActionException() {
		this.message = "You cannot do that right now.";
	}
	
	public UnauthorizedActionException(String msg) {
		this.message = msg;
	}

	@Override
	public String getMessage() {
		return ChatColor.RED + message;
	}
	
}