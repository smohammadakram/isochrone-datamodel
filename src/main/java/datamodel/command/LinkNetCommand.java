package datamodel.command;

import datamodel.database.DBConnector;
import datamodel.timeexpanded.linknetwork.LinkNetwork;

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
		new LinkNetwork(new DBConnector(), city).mapping();
	}

}
