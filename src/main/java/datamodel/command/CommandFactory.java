package datamodel.command;

public abstract class CommandFactory {
	enum Command {
		VDVNAMES,
		TIME_DEP,
		TIME_EXP,
		TMPDB,
		LINKNET,
		PBFPARSER,
		SCRIPTGEN,
		BUSNET,
		TEST,
		LINKSCRIPT;
	}

	// Public static factory method

	public static ICommand createCommand(final String commandName, final String... args) {
		if (commandName == null) {
			throw new NullPointerException("Command name must be non-null to create a instance from it!");
		}

		return createCommand(Command.valueOf(commandName.toUpperCase().replaceAll("-", "_")), args);
	}

	// Package-private factory method (using the command object)

	static ICommand createCommand(final Command cmd, final String... args) {
		ICommand cmdO = null;

		switch(cmd) {
			case BUSNET:
				cmdO = new BusNetCommand(args[0], args[1]);
				break;
			case LINKSCRIPT:
				cmdO = new LinkScriptCommand(args[0], args[1]);
				break;
			case PBFPARSER:
				cmdO = new PbfParserCommand(args[0], args[1], args[2]);
				break;
			case SCRIPTGEN:
				cmdO = new ScriptGenCommand(args[0], args[1]);
				break;
			case TIME_DEP:
				cmdO = new GenerateTableDep(args[0], args[1]);
				break;
			case TIME_EXP:
				cmdO = new GenerateTableExp(args[0], args[1]);
				break;
			case TMPDB:
				cmdO = new TmpDbCommand(args[0], args[1]);
				break;
			case VDVNAMES:
				cmdO = new VdvNameCommand(args[0]);
				break;
			default:
				cmdO = new TestCommand(args[0], args[1]);
		}

		return cmdO;
	}

}
