package datamodel.command;

import datamodel.db.DbConnector;
import datamodel.impl.link.LinkNetwork;

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
		try (final DbConnector db = new DbConnector()) {
			final LinkNetwork ln = new LinkNetwork(db, city);
			CommandUtils.uncheck(ln::performMapping);
		}
	}

}
