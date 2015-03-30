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
		this.folder = folder + File.separatorChar;
	}

	// Getter

	public String getCity() {
		return city;
	}

	public String getFolder() {
		return folder;
	}

	// Protected methods

	protected void createSqlFile(final String filename) throws IOException {
		final ScriptGenerator sg = new ScriptGenerator(filename, "<city>", city);
		sg.writeScript(folder + city + "_" + filename);
	}
}
