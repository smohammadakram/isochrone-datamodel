package datamodel.command;

import datamodel.linknetwork.LinkNetwork;
import datamodel.util.DBConnector;

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
		final LinkNetwork ln = new LinkNetwork(new DBConnector(), city);
		ln.performMapping();

		final long end = System.currentTimeMillis();
		final DateFormat df = new SimpleDateFormat("mm:ss");
		System.out.println("[INFO] Time for building link network: " + df.format(new Date((end - start))) + " minutes");
	}

}
