package net.propvp.practice.command.exception.type;

import org.bukkit.ChatColor;

public class IllegalCharacterException extends UsageException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return ChatColor.RED + "You have input an illegal character. You might need to provide a number.";
	}
	
}