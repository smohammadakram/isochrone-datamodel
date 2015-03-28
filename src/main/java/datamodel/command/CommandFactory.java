package datamodel.command;

public abstract class CommandFactory {
	enum Command {
		BUSNET,
		LINKNET,
		LINKSCRIPT,
		STREETNET,
		SCRIPTGEN,
		TMPDB,
		VDVNAMES;
	}

	// Public static factory method

	public static ICommand createCommand(final String commandName, final String... args) {
		if (commandName == null) {
			throw new NullPointerException("Command name must be non-null to create a instance from it!");
		}

		final Command cmd = Command.valueOf(commandName.toUpperCase().replaceAll("-", "_"));
		return createCommand(cmd, args);
	}

	// Package-private factory method (using the command object)

	static ICommand createCommand(final Command cmd, final String... args) {
		ICommand cmdO = null;

		switch (cmd) {
			case BUSNET:
				cmdO = new BusNetCommand(args[0], args[1]);
				break;
			case LINKSCRIPT:
				cmdO = new LinkScriptCommand(args[0], args[1]);
				break;
			case SCRIPTGEN:
				cmdO = new ScriptGenCommand(args[0], args[1]);
				break;
			case STREETNET:
				cmdO = new StreetNetCommand(args[0], args[1], args[2]);
				break;
			case TMPDB:
				cmdO = new TmpDbCommand(args[0], args[1]);
				break;
			default:
				cmdO = new VdvNameCommand(args[0]);
		}

		return cmdO;
	}

}
