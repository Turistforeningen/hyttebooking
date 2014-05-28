package JSONFormatters;

public class CabinJson {
	public String type;
	public String name;
	public int beds;
	public long id;
	
	public CabinJson(String type, String name, int beds, long id) {
		this.type = type;
		this.name = name;
		this.beds = beds;
		this.id	  = id;
	}
}
