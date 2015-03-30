package datamodel.impl.bus;

import java.util.Calendar;
import java.util.GregorianCalendar;

class Service {

	private static final int LENGTH_OF_YEAR = 366;
	private String endDate;
	private int id;
	private String startDate;
	private boolean[] validity;

	// Constructor

	public Service(final int id) {
		this.id = id;
	}

	// Getter

	public String getEndDate() {
		return endDate;
	}

	public int getId() {
		return id;
	}

	public String getStartDate() {
		return startDate;
	}

	public boolean[] getValidity() {
		return validity;
	}

	public String getValidityVector() {
		if (validity == null || validity.length <= 0) {
			return null;
		}

		final Calendar end = new GregorianCalendar();
		end.setFirstDayOfWeek(Calendar.MONDAY);
		end.setTimeInMillis(parseDateToMillis(endDate));
		final int doyEnd = end.get(Calendar.DAY_OF_YEAR);

		final Calendar start = new GregorianCalendar();
		start.setFirstDayOfWeek(Calendar.MONDAY);
		start.setTimeInMillis(parseDateToMillis(startDate));
		int doyStart = start.get(Calendar.DAY_OF_YEAR);

		final Calendar c = (Calendar) start.clone();
		final int[] validityVector = new int[LENGTH_OF_YEAR];
		while (doyStart <= doyEnd) {
			if (validity[c.get(Calendar.DAY_OF_WEEK) - 1]) {
				validityVector[doyStart - 1] = 1;
			}
			c.add(Calendar.DAY_OF_YEAR, +1);
			doyStart = c.get(Calendar.DAY_OF_YEAR);
		}

		final StringBuilder dbVector = new StringBuilder();
		for (int i = 0; i < validityVector.length; i++) {
			dbVector.append(validityVector[i]);
		}

		return dbVector.toString();
	}

	// Setter

	public void setEndDate(final String endDate) {
		this.endDate = endDate;
	}

	public void setStartDate(final String sDate) {
		startDate = sDate;
	}

	public void setValidity(final boolean... validity) {
		this.validity = validity;
	}

	// Private static methods

	private static long parseDateToMillis(final String d) {
		final Calendar c = new GregorianCalendar(Integer.parseInt(d.substring(0, 4)), Integer.parseInt(d.substring(4, 6)), Integer.parseInt(d.substring(6, 8)));
		return c.getTimeInMillis();
	}

}
