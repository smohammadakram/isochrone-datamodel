package data_model.dijkstra;

public class Vertex {

	private final String id;
	private final int mode;
	private final String name;

	// Constructor

	public Vertex(final String id, final String name) {
		this.id = id;
		this.name = name;
		mode = -1;
	}

	public Vertex(final String id, final String name, final int mode) {
		this.id = id;
		this.name = name;
		this.mode = mode;
	}

	// Getters

	public String getId() {
		return id;
	}

	public int getMode() {
		return mode;
	}

	public String getName() {
		return name;
	}

	// Public methods

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Vertex other = (Vertex) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return name;
	}

}
