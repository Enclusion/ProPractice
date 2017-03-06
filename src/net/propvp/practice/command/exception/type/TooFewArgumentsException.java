package net.propvp.practice.command.exception.type;

import org.bukkit.ChatColor;

public class TooFewArgumentsException extends UsageException {

	private static final long serialVersionUID = 1L;

	private String message;
	
	public TooFewArgumentsException() {
		message = "You did not provide enough arguments.";
	}
	
	public TooFewArgumentsException(String msg) {
		this.message = msg;
	}
	
	@Override
	public String getMessage() {
		return ChatColor.RED + message;
	}
	
}