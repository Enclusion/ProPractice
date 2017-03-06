package net.propvp.practice.command.exception;

import net.propvp.practice.command.exception.type.TooFewArgumentsException;
import net.propvp.practice.command.exception.type.TooManyArgumentsException;

public class CommandException extends Exception {

	private static final long serialVersionUID = 1L;

	public static CommandException makeArgumentException(int expectedCount, int actualCount) {
		return (actualCount > expectedCount) ? new TooManyArgumentsException() : new TooFewArgumentsException();
	}
	
}
