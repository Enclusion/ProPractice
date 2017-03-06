package net.propvp.practice.command.exception.type;

import org.bukkit.ChatColor;

public class DoesNotMatchException extends UsageException {

	private static final long serialVersionUID = 1L;
	
	private String message;
	
	public DoesNotMatchException() {
		this.message = "That object does not exist.";
	}
	
	public DoesNotMatchException(String msg) {
		this.message = msg;
	}

	@Override
	public String getMessage() {
		return ChatColor.RED + message;
	}
	
}