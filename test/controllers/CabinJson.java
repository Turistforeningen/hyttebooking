package controllers;

public class CabinJson {
	public String type;
	public String name;
	public int beds;
	public int id;
	
	public CabinJson(String type, String name, int beds, int id) {
		this.type = type;
		this.name = name;
		this.beds = beds;
		this.id	  = id;
	}
}
