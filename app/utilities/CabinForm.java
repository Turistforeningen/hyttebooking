package utilities;

import models.Cabin;
import models.LargeCabin;
import models.SmallCabin;
import flexjson.JSONDeserializer;

public class CabinForm extends AbstractForm<Cabin>{
	
	public String type;
	public String name;
	public int beds;
	public int id;
	
	public CabinForm() {
		
	}
	
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
	
	@Override
	public Cabin createModel() {
		if(validate()) {
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
