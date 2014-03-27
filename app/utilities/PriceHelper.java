package utilities;

import com.fasterxml.jackson.databind.JsonNode;

public class PriceHelper {
	
	public static double calculateAmount(JsonNode guestList, int days) {
		double amount = 0;
		//Temporarily gets prices from json string, but should get them from database
		//based on type, to avoid price-changing frontend.
		for(JsonNode guestType : guestList) {
			amount += days*guestType.get("nr").asInt()*guestType.get("price").asDouble();
		}
		
		return amount;
	}
}
