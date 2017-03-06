package net.propvp.practice.command.exception.type;

import org.bukkit.ChatColor;

public class AlreadyExistsException extends UsageException {

	private static final long serialVersionUID = 1L;
	
	private String message;
	
	public AlreadyExistsException() {
		this.message = "That object already exists.";
	}
	
	public AlreadyExistsException(String msg) {
		this.message = msg;
	}

	@Override
	public String getMessage() {
		return ChatColor.RED + message;
	}
	
}