package datamodel.command;

import datamodel.impl.ScriptGenerator;

import java.io.File;
import java.io.IOException;

abstract class AbstractScriptCommand implements ICommand {
	private String folder;
	private String city;

	// Constructor

	protected AbstractScriptCommand(final String folder, final String city) {
		this.city = city;
		this.folder = folder;
	}

	// Getter

	public String getCity() {
		return city;
	}

	public String getFolder() {
		return folder;
	}

	// Protected methods

	protected void createSqlFile(final String path) throws IOException {
		final String filename = new File(path).getName();
		final ScriptGenerator sg = new ScriptGenerator(path, "<city>", city);
		sg.writeScript(folder + File.separatorChar + city + "_" + filename);
	}
}
