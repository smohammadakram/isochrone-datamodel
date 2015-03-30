package datamodel.command;

import datamodel.tmpdb.BusDataParser;
import datamodel.util.DbConnector;

/**
 * This populates the temporary bus database.
 */
public class BusNetCommand implements ICommand {
	private final String city;
	private final String gtfs;

	public BusNetCommand(final String gtfs, final String city) {
		super();
		this.city = city;
		this.gtfs = gtfs;
	}

	@Override
	public void execute() {
		final DbConnector db = new DbConnector();
		final BusDataParser bdp = new BusDataParser(db, gtfs, city);
		new Thread(() -> bdp.parseRoutes()).start();
		new Thread(() -> bdp.parseTrips()).start();
		new Thread(() -> { bdp.parseCalendar(); bdp.createCalendar(); }).start();
		new Thread(() -> bdp.parseStops()).start();
		new Thread(() -> bdp.parseTripSequence()).start();
	}

}
