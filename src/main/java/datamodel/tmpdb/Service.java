package datamodel.tmpdb;

class Service {

	private String endDate;
	private int id;
	private String startDate;
	private boolean[] validity;

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

	public void setEndDate(final String endDate) {
		this.endDate = endDate;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setStartDate(final String sDate) {
		startDate = sDate;
	}

	public void setValidity(final boolean... validity) {
		this.validity = validity;
	}

}
