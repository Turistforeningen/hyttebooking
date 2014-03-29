package utilities;

import models.Cabin;
import models.LargeCabin;
import models.SmallCabin;
import flexjson.JSONDeserializer;

/**
 * Subclass of AbstractForm. Binds and validate json data used to create
 * a Cabin in the booking system.
 * @author Olav
 *
 */
public class CabinForm extends AbstractForm<Cabin>{
	
	public String type;
	public String name;
	public int beds;
	public int id;
	
	/**
	 * FlexJson needs an constructor even if its empty
	 */
	public CabinForm() {
		
	}
	
	/**
	 * CreateModel creates an Cabin and saves it to the database.
	 * If the form is not valid, it returns null.
	 */
	@Override
	public boolean validate() {
		if(type == null) {
			System.out.println("hei");
			this.addError("No type paramter in Json");
			return false;
		}
		
		if(!(type.equals("LargeCabin") || type.equals("SmallCabin"))) {
			this.addError("Name parameter invalid");
			return false;
		}
		
		if(name == null) {
			this.addError("No name parameter in Json");
			return false;
		}
		
		return true;	
	}
	
	/**
	 * Validate is responsible for checking that no data is missing from the json request,
	 * and that data is correctly inputed. If there is any missing or wrong
	 * parameters, then the form is deemed invalid.
	 */
	@Override
	public Cabin createModel() {
		if(isValid()) {
			if(this.type.equals("SmallCabin")) {
				SmallCabin cabin = new SmallCabin(this.name);
				cabin.save();
				return cabin;
			}
			else if(this.type.equals("LargeCabin")) {
				LargeCabin cabin = new LargeCabin(this.name, this.beds);
				cabin.save();
				return cabin;
			}
			
		}
		return null;
	}
	
	public String toString() {
		return "type: " +type + " ,name: " +name +" ,beds: " + beds + " ,id: " +id;
	}
	
	/**
	 * jsonFlex is used to deserialize/unmarshall json String into a form containing
	 * java classes like String, int, List<T>. If the deserializer cant complete,
	 * it is counted as an error.  
	 * 
	 * @param jsonBooking
	 * @return CabinForm used to validate and bind the data.
	 */
	public static CabinForm deserializeJson(String jsonCabin) {
		CabinForm cabinData = null;
		
		try {
			JSONDeserializer<CabinForm> deserializer = new JSONDeserializer<CabinForm>();
			cabinData = deserializer.deserialize(jsonCabin , CabinForm.class);
		} catch (Exception e) {
			//add more detailed data. Possibly what variable it cant find.
			cabinData = new CabinForm();
			cabinData.addError("Could not parse data");
			
		}
		return cabinData;
	}

	

	
}