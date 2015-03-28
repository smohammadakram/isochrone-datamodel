package datamodel.command;

import datamodel.database.ScriptGenerator;
import datamodel.timedependent.database.TimeDependentSQL;

import java.io.IOException;

public class TestCommand implements ICommand {
	private final String city;
	private final String folder;

	public TestCommand(final String folder, final String city) {
		super();
		this.city = city;
		this.folder = folder;
	}

	@Override
	public void execute() throws IOException {
		final ScriptGenerator sg0 = new ScriptGenerator(TimeDependentSQL.CREATE_TABLES, "<city>", city);
		sg0.writeScript(folder + "/" + city + "-network.sql");

		final ScriptGenerator sg1 = new ScriptGenerator(TimeDependentSQL.NODES_EDGES_VIEWS, "<city>", city);
		sg1.writeScript(folder + "/" + city + "-views.sql");

		final ScriptGenerator sg2 = new ScriptGenerator(TimeDependentSQL.INSERT, "<city>", city);
		sg2.writeScript(folder + "/" + city + "-insert.sql");
	}

}
