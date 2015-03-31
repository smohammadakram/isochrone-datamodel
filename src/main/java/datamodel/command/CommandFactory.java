package datamodel.command;

import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class CommandFactory {

	private static final Logger LOGGER = LogManager.getLogger(CommandFactory.class);
	private enum Command {
		BUSNET,
		BUSSCRIPT,
		LINKNET,
		LINKSCRIPT,
		STREETNET,
		STREETSCRIPT
	}

	// Public static factory method

	public static ICommand createCommand(final String commandName, final String... args) {
		if (commandName == null) {
			throw new NullPointerException("Command name must be non-null to create a instance from it!");
		}

		final Command cmd = Command.valueOf(commandName.toUpperCase(Locale.ENGLISH).replaceAll("-", "_"));
		return createCommand(cmd, args);
	}

	// Package-private factory method (using the command object)

	static ICommand createCommand(final Command cmd, final String... args) {
		LOGGER.info("Got command {}", cmd.name());
		ICommand cmdO = null;

		switch (cmd) {
			case BUSSCRIPT:
				cmdO = new BusScriptCommand(args[0], args[1]);
				break;
			case LINKNET:
				cmdO = new LinkNetCommand(args[0]);
				break;
			case LINKSCRIPT:
				cmdO = new LinkScriptCommand(args[0], args[1]);
				break;
			case STREETNET:
				cmdO = new StreetNetCommand(args[0], args[1], args[2]);
				break;
			case STREETSCRIPT:
				cmdO = new StreetScriptCommand(args[0], args[1]);
				break;
			case BUSNET:
			default:
				cmdO = new BusNetCommand(args[0], args[1]);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Created class {} from command", cmdO.getClass().getSimpleName());
		}

		return cmdO;
	}

}
