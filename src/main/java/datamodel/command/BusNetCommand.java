package datamodel.command;

import datamodel.command.CommandUtils.Supplier_WithExceptions;
import datamodel.db.DbConnector;
import datamodel.impl.bus.BusNetwork;

/**
 * This populates the temporary bus database.
 */
public class BusNetCommand implements ICommand {
	private final String city;
	private final String gtfs;

	// Constructor

	public BusNetCommand(final String gtfs, final String city) {
		super();
		this.city = city;
		this.gtfs = gtfs;
	}

	// Public methods

	@Override
	public void execute() {
		try(final DbConnector db = new DbConnector()) {
			final BusNetwork bn = new BusNetwork(db, gtfs, city);

			new Thread(() -> executeSQL(db, bn::parseRoutes)).start();
			new Thread(() -> executeSQL(db, bn::parseTrips)).start();
			new Thread(() -> { executeSQL(db, bn::parseCalendar); CommandUtils.uncheck(bn::createCalendar); }).start();
			new Thread(() -> executeSQL(db, bn::parseStops)).start();
			new Thread(() -> executeSQL(db, bn::parseTripSequence)).start();
		}
	}

	// Private static methods

	private static void executeSQL(final DbConnector db, final Supplier_WithExceptions<String> supplier) {
		final String sql = CommandUtils.uncheck(supplier);
		CommandUtils.rethrowConsumer(db::execute).accept(sql);;
	}
}
