package datamodel.command;

import datamodel.util.ScriptGenerator;

import java.io.File;
import java.io.IOException;

abstract class AbstractSqlCommand implements ICommand {
	private String folder;
	private String city;

	// Constructor

	protected AbstractSqlCommand(final String folder, final String city) {
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

	protected void createSqlFiles(final String... filenames) throws IOException {
		for (final String f : filenames) {
			final ScriptGenerator sg = new ScriptGenerator(new File("src/main/resources/" + f), "<city>", city);
			sg.writeScript(folder + "/" + city + "_" + f);
		}
	}
}
