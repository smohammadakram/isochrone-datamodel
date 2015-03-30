package datamodel.command;

import datamodel.db.DbConnector;
import datamodel.impl.link.LinkNetwork;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This creates the link network.
 */
public class LinkNetCommand implements ICommand {
	private final String city;

	public LinkNetCommand(final String city) {
		super();
		this.city = city;
	}

	@Override
	public void execute() {
		final long start = System.currentTimeMillis();
		try (final DbConnector db = new DbConnector()) {
			final LinkNetwork ln = new LinkNetwork(db, city);
			CommandUtils.uncheck(ln::performMapping);
		}

		final long end = System.currentTimeMillis();
		final DateFormat df = new SimpleDateFormat("mm:ss");
		System.out.println("[INFO] Time for building link network: " + df.format(new Date((end - start))) + " minutes");
	}

}
