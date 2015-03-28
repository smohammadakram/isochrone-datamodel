package datamodel;

import datamodel.command.CommandFactory;

import java.io.IOException;
import java.util.Arrays;

public final class Main {

	private Main() { }

	public static void main(final String[] args) throws IOException {
		final String cmdName = args[0].toUpperCase();
		final String[] cmdParams = Arrays.copyOfRange(args, 1, args.length);

		CommandFactory.createCommand(cmdName, cmdParams).execute();
	}

}
