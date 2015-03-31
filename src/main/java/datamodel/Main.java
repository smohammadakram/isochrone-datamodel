package datamodel;

import datamodel.command.CommandFactory;

import java.util.Arrays;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Main {
	private static final Logger LOGGER = LogManager.getLogger(Main.class);
	private static final String PROGRAM_NAME = "isochrone-datamodel";
	private static final double TIME_CONVERSION_FACTOR = 1000.0d;

	private Main() { }

	public static void main(final String[] args) {
		final long l0 = System.currentTimeMillis();
		LOGGER.info("Starting program " + PROGRAM_NAME);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Parameters are:");
			Arrays.stream(args).forEach(a -> LOGGER.debug(" - " + a));
		}

		final String cmdName = args[0].toUpperCase(Locale.ENGLISH);
		final String[] cmdParams = Arrays.copyOfRange(args, 1, args.length);

		CommandFactory.createCommand(cmdName, cmdParams).execute();
		LOGGER.info("Exiting program " + PROGRAM_NAME);
		LOGGER.info("Execution time was " + ((System.currentTimeMillis() - l0) / TIME_CONVERSION_FACTOR) + " seconds");
	}

}
