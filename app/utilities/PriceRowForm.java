package utilities;

import play.i18n.Messages;
import flexjson.JSONDeserializer;
import models.Price;

public class PriceRowForm extends AbstractForm<Price> {
	public double nonMemberPrice;
	public double memberPrice;
	public String guestType;
	public String ageRange;
	public boolean isMinor;
	
	private boolean isValid = false;
	@Override
	public Price createModel() {
		if(isValid || validate()) {
			Price p = new Price(this.guestType, this.ageRange, this.nonMemberPrice, this.memberPrice, this.isMinor);
			return p;
		}
		return null;
	}

	@Override
	public boolean validate() {
		if(nonMemberPrice < 0) {
			addError("ikke-medlemspris ugyldig");
			return false;
		}
		
		if(memberPrice <0) {
			addError("medlemspris ugyldig");
			return false;
		}
		
		if(guestType == null || guestType.length() == 0) {
			addError("Ikke satt gjesteType");
			return false;
		}
		
		this.isValid = true;
		return true;
	}
	
	public static PriceRowForm deserializeJson(String jsonBooking) {
		PriceRowForm priceData = null;
		System.out.println(jsonBooking);
		try {
			JSONDeserializer<PriceRowForm> deserializer = new JSONDeserializer<PriceRowForm>();
			priceData = deserializer.deserialize(jsonBooking , PriceRowForm.class);
		} catch (Exception e) {
			e.printStackTrace();
			//add more detailed data. Possibly what variable it cant find.
			System.out.println(e.getMessage());
			priceData = new PriceRowForm();
			priceData.addError(Messages.get("json.couldNotParseData"));
			priceData.validationError = true;
			
		}
		return priceData;
	}
}
