package datamodel.command;

import java.io.File;
import java.util.StringTokenizer;

/**
 * This renames the vdv file from *.x10 to *.X10.
 */
public class VdvNameCommand implements ICommand {
	private final String city;

	public VdvNameCommand(final String city) {
		super();
		this.city = city;
	}

	@Override
	public void execute() {
		System.out.println("[INFO] File renaming.");
		final File[] files = new File(city).listFiles();
		if (files == null) {
			return;
		}

		for (final File f : files) {
			final StringTokenizer st = new StringTokenizer(f.getName(), ".");
			final String name = st.nextToken();
			final String newName = city + "/" + name + ".x10";
			final boolean renamed = f.renameTo(new File(newName));
			if (renamed) {
				System.out.println("[DEBUG] File renamed to \"" + newName + "\"");
			} else {
				System.out.println("[DEBUG] Could not rename file to \"" + newName + "\"");
			}
		}
	}

}
