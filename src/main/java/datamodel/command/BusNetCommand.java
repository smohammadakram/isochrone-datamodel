package datamodel.command;

import datamodel.command.CommandUtils.SupplierWithExceptions;
import datamodel.db.DbConnector;
import datamodel.impl.bus.BusNetwork;

import java.util.Arrays;

/**
 * This populates the temporary bus database.
 */
public class BusNetCommand implements ICommand {
	private final String city;
	private final String folder;

	// Constructor

	public BusNetCommand(final String folder, final String city) {
		super();
		this.city = city;
		this.folder = folder;
	}

	// Public methods

	@Override
	public void execute() {
		try (final DbConnector db = new DbConnector()) {
			final BusNetwork bn = new BusNetwork(db, folder, city);

			// create threads collection
			final Thread[] threads = new Thread[] {
				new Thread(() -> executeSQL(db, bn::parseRoutes)),
				new Thread(() -> executeSQL(db, bn::parseTrips)),
				new Thread(() -> {
					executeSQL(db, bn::parseCalendar);
					executeSQL(db, bn::parseCalendarDates);
					CommandUtils.rethrowConsumer(bn::copyBusCalendarTable).accept(false);
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
		CommandUtils.rethrowConsumer(db::executeBatch).accept(sql.split(";"));
	}
}
