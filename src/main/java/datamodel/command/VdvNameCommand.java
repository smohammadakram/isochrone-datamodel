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
		System.out.println("[INFO] File rename.");
		final File vdv = new File(city);
		final File[] files = vdv.listFiles();
		for (final File f : files) {
			final StringTokenizer st = new StringTokenizer(f.getName(), ".");
			final String name = st.nextToken();
			f.renameTo(new File(city + "/" + name + ".x10"));
		}
	}

}
