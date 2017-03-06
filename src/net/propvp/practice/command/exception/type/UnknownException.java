package net.propvp.practice.command.exception.type;

import org.bukkit.ChatColor;

public class UnknownException extends UsageException {

	private static final long serialVersionUID = 1L;
	
	private String message;
	
	public UnknownException() {
		this.message = "That object already exists.";
	}
	
	public UnknownException(String msg) {
		this.message = msg;
	}

	@Override
	public String getMessage() {
		return ChatColor.RED + message;
	}
	
}