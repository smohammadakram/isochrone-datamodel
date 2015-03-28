package datamodel.command;

import datamodel.database.DBConnector;
import datamodel.timeexpanded.busnetwork.BusDataParser;

/**
 * This populates the temporary bus database.
 */
public class TmpDbCommand implements ICommand {
	private final String city;
	private final String gtfs;

	public TmpDbCommand(final String gtfs, final String city) {
		super();
		this.city = city;
		this.gtfs = gtfs;
	}

	@Override
	public void execute() {
		final DBConnector db = new DBConnector();
		final BusDataParser bdp = new BusDataParser(db, gtfs, city);
		new Thread(() -> bdp.parseRoutes()).start();
		new Thread(() -> bdp.parseTrips()).start();
		new Thread(() -> { bdp.parseCalendar(); bdp.createCalendar(); }).start();
		new Thread(() -> bdp.parseStops()).start();
		new Thread(() -> bdp.parseTripSequence()).start();
	}

}
