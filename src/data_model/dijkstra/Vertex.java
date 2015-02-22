package data_model.dijkstra;

public class Vertex {
	  final private String id;
	  final private String name;
	  private final int mode;	  
	  
	  public Vertex(String id, String name) {
	    this.id = id;
	    this.name = name;
	    mode = -1;
	  }
	  
	  public Vertex(String id, String name, int mode){
		  this.id = id;
		  this.name = name;
		  this.mode = mode;
	  }
	  public String getId() {
	    return id;
	  }

	  public String getName() {
	    return name;
	  }
	  
	  @Override
	  public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((id == null) ? 0 : id.hashCode());
	    return result;
	  }
	  
	  @Override
	  public boolean equals(Object obj) {
	    if (this == obj)
	      return true;
	    if (obj == null)
	      return false;
	    if (getClass() != obj.getClass())
	      return false;
	    Vertex other = (Vertex) obj;
	    if (id == null) {
	      if (other.id != null)
	        return false;
	    } else if (!id.equals(other.id))
	      return false;
	    return true;
	  }

	  @Override
	  public String toString() {
	    return name;
	  }
	  
	public int getMode() {
		return mode;
	}
	  
	} 