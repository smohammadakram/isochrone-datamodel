package datamodel.command;

import datamodel.timedependent.database.TimeDependentSQL;
import datamodel.timeexpanded.database.ScriptGenerator;

public class TestCommand implements ICommand {
	private final String city;
	private final String folder;

	public TestCommand(final String folder, final String city) {
		super();
		this.city = city;
		this.folder = folder;
	}

	@Override
	public void execute() {
		final ScriptGenerator sg0 = new ScriptGenerator(folder + "/" + city + "-network.sql");
		sg0.createByReplace(TimeDependentSQL.CREATE_TABLES, "<city>", city);
		sg0.writeScipt();
		sg0.closeWriter();

		final ScriptGenerator sg1 = new ScriptGenerator(folder + "/" + city + "-views.sql");
		sg1.createByReplace(TimeDependentSQL.NODES_EDGES_VIEWS, "<city>", city);
		sg1.writeScipt();
		sg1.closeWriter();

		final ScriptGenerator sg2 = new ScriptGenerator(folder + "/" + city + "-insert.sql");
		sg2.createByReplace(TimeDependentSQL.INSERT, "<city>", city);
		sg2.writeScipt();
		sg2.closeWriter();
	}

}
