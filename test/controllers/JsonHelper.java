package controllers;

import java.util.ArrayList;
import java.util.List;

import models.RDate;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import flexjson.JSONSerializer;
import flexjson.transformer.AbstractTransformer;
import play.libs.Json;
import utilities.DateHelper;

/**
 * Make sure largeCabin with id 1 and following prices exists!
 * WARNING: IF PRICES CHANGE FOR CABIN 1; TEST STATIC DATA MUST CHANGE, CHANGE THE STRINGS BELOW TO MATCH
 */
public class JsonHelper {
	
	

	public final static String[] GUEST_TYPE_NAMES = {"Voksen, medlem","Ungdom, medlem","Barn, medlem","Spedbarn, medlem", "Voksen,", "Ungdom,", "Barn,", "Spedbarn,"};
	public final static String[] AGE_RANGE_NAMES = {"26 og opp", "13-25", "4-12", "0-4", "26 og opp", "13-25", "4-12", "0-4"};
	public final static double[] GUEST_PRICES = {300.0, 200.0, 100.0, 0.0, 400.0, 300.0, 200.0, 0.0};
	public final static boolean[] MEMBERSHIP = {true, true, true, true, false, false, false, false};
	
	
	
	/**
	 * Creates a valid cabin with id 1, reservation lasting 7 days and guests
	 */
	private static JsonNode getValidCabinJSON(ArrayList<GuestJson> guests) {
		JSONSerializer ser = new JSONSerializer();
		ser.include("CabinJson") //, "dateTo", "dateFrom", "guests", "termsAndConditions", "guests.id", "guests.ageRange", "guests.guestType", "guests.nr", "guests.price", "guests.isMember")
		.exclude("*.class")
		.transform(new DateTimeTransfomer2(), DateTime.class);

		CabinJson cj = new CabinJson((long)1, RDate.fDt.plusWeeks(1), RDate.fDt.plusWeeks(2), guests, true);
		ObjectNode node = Json.newObject();
		String cjSerial = ser.serialize(cj);
		System.out.println("SERIALIZED CABIN: "+cjSerial);
		node.put("", cjSerial);
		return node;
	}
	
	/**
	 * Creates invalid cabin due to termsAndConditions false
	 */
	private static JsonNode getInvalidCabinJson(ArrayList<GuestJson> guests) {
		JSONSerializer ser = new JSONSerializer();
		ser.include("CabinJson") //, "dateTo", "dateFrom", "guests", "termsAndConditions", "guests.id", "guests.ageRange", "guests.guestType", "guests.nr", "guests.price", "guests.isMember")
		.exclude("*.class")
		.transform(new DateTimeTransfomer2(), DateTime.class);
		
		CabinJson cj = new CabinJson((long)1, RDate.fDt.plusWeeks(1), RDate.fDt.plusWeeks(2), guests, false);
		ObjectNode node = Json.newObject();
		String cjSerial = ser.serialize(cj);
		node.put("", cjSerial);
		return node;
	}

	//example of a faulty json to be tested, similar methods to be created
	public JsonNode getOnlyMemberBabiesBookingJSON() {
		int[] nrOfGuests = {0, 0, 0, 3, 0, 0, 0, 0}; //specify here how many of each type of guest you want
		//since this is a only baby booking, we set the nr of baby guests to 3 (arbitrary number)
		
		ArrayList<GuestJson> guests = GuestJson.addGuests(nrOfGuests);
		JsonNode onlyBabiesJson = getValidCabinJSON(guests);
		
		return onlyBabiesJson;
	}
	
	public static JsonNode getOkBooking() {
		int[] nrOfGuests = {2, 0, 0, 0, 0, 0, 0, 0}; //2 member adults, OK
		ArrayList<GuestJson> guests = GuestJson.addGuests(nrOfGuests);
		JsonNode okBooking = getValidCabinJSON(guests);
		
		System.out.println("RETURNING OK BOOKING with SIZE: "+okBooking.size());
		
		return okBooking; 
	}
	
	public static JsonNode getInvalidCabinBooking() {
		int[] nrOfGuests = {2, 0, 0, 0, 0, 0, 0, 0}; //2 member adults, OK
		ArrayList<GuestJson> guests = GuestJson.addGuests(nrOfGuests);
		JsonNode invalidBooking = getInvalidCabinJson(guests);
		
		return invalidBooking; 
	}
	//INSERT MORE METHODS PRODUCING DIFFERENT JSONS HERE
	
	//TODO method getOnlyNonMemberBabies
	
	//TODO method getNoGuests, guest nr all 0
	
	
	
	///END
	
	/** HELPER CLASSES **/
	static class CabinJson {
		/* EXAMPLE
		 * "cabinId":"1",
	   "dateTo":"2014-05-22",
	   "dateFrom":"2014-05-15",
	   "guests":[
		 */
		
		long cabinId;
		DateTime dateTo;
		DateTime dateFrom;
		List<GuestJson> guests = new ArrayList<GuestJson>();
		boolean termsAndConditions;
		
		public CabinJson(long cabinId, DateTime dateTo, DateTime dateFrom, List<GuestJson> gJson, boolean termsAndConditions) {
			this.cabinId = cabinId;
			this.dateTo = dateTo;
			this.dateFrom = dateFrom;
			this.guests.addAll(gJson);
			this.termsAndConditions = termsAndConditions;
		}
	}

	static class GuestJson {
		/* EXAMPLE
		 * "guests":[
	      {
	         "id":1,
	         "ageRange":"26 og opp",
	         "guestType":"Voksen, medlem",
	         "nr":3,
	         "price":300,
	         "isMember":true
	      },
		 */
		long id;
		String ageRange;
		String guestType;
		long nr;
		double price;
		boolean isMember;
		
		public GuestJson(long id, String ageRange, String guestType, long nr, double price, boolean isMember) {
			this.id = id;
			this.ageRange = ageRange;
			this.guestType = guestType;
			this.nr = nr;
			this.price = price;
			this.isMember = isMember;
		}
		
		public static ArrayList<GuestJson> addGuests(int[] nrOfGuests) {
			if(nrOfGuests.length < GUEST_TYPE_NAMES.length) {
				System.err.println("ERROR: nrOfGuests array too short!");
				return null;
			}
			ArrayList<GuestJson> guests = new ArrayList<GuestJson>(); 
			for(int i = 0; i<GUEST_TYPE_NAMES.length; i++) {
				System.out.println("ADDING: "+1+((long)i%4)+" - "+AGE_RANGE_NAMES[i]+" - "+GUEST_TYPE_NAMES[i]+" - "+nrOfGuests[i]+" - "+GUEST_PRICES[i]+" - "+MEMBERSHIP[i]);
				guests.add(new GuestJson(1+((long)i%4) , AGE_RANGE_NAMES[i], GUEST_TYPE_NAMES[i], nrOfGuests[i], GUEST_PRICES[i], MEMBERSHIP[i]));
			}
			return guests;
		}
	}
	

}