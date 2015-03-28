package datamodel;

import datamodel.command.CommandFactory;
import datamodel.command.ICommand;

import java.util.Arrays;

public class Main {

	public static void main(final String[] args) {
		final String cmdName = args[0].toUpperCase();
		final String[] cmdParams = Arrays.copyOfRange(args, 1, args.length);
		final ICommand cmd = CommandFactory.createCommand(cmdName, cmdParams);
		cmd.execute();
	}

}
