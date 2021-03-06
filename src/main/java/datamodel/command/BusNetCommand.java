package datamodel.command;

import datamodel.command.CommandUtils.SupplierWithExceptions;
import datamodel.db.DbConnector;
import datamodel.db.DbScript;
import datamodel.impl.bus.BusNetwork;

import java.util.Arrays;

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
		try (final DbConnector db = new DbConnector()) {
			final BusNetwork bn = new BusNetwork(db, city);
			CommandUtils.rethrowConsumer(bn::initGtfs).accept(gtfs);

			// create threads collection
			final Thread[] threads = new Thread[] {
				new Thread(() -> executeSQL(db, bn::parseRoutes)),
				new Thread(() -> executeSQL(db, bn::parseTrips)),
				new Thread(() -> {
					executeSQL(db, bn::parseCalendar);
					executeSQL(db, bn::parseCalendarDates);
					CommandUtils.rethrowConsumer(bn::insertBusCalendar).accept(false);
				}),
				new Thread(() -> executeSQL(db, bn::parseStops)),
				new Thread(() -> executeSQL(db, bn::parseStopTimes))
			};

			// start and wait for threads
			Arrays.stream(threads).forEach(t -> t.start());
			Arrays.stream(threads).forEach(CommandUtils.rethrowConsumer(t -> t.join()));
		}
	}

	// Private static methods

	private static void executeSQL(final DbConnector db, final SupplierWithExceptions<String> supplier) {
		final String sql = CommandUtils.uncheck(supplier);
		CommandUtils.rethrowConsumer(db::executeScript).accept(new DbScript(sql, true));
	}
}
