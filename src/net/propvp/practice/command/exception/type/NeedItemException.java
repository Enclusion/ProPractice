package net.propvp.practice.command.exception.type;

import org.bukkit.ChatColor;

public class NeedItemException extends UsageException {

	private static final long serialVersionUID = 1L;
	
	private String message;
	
	public NeedItemException() {
		message = ChatColor.RED + "You do not have that item in your inventory.";
	}
	
	public NeedItemException(String s) {
		message = ChatColor.RED + s;
	}

	@Override
	public String getMessage() {
		return message;
	}
	
}