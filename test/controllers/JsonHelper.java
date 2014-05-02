package controllers;

import java.util.ArrayList;
import java.util.List;

import models.RDate;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import flexjson.JSONSerializer;
import play.libs.Json;

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
	private static String getValidCabinJSON(ArrayList<GuestJson> guests) {
		JSONSerializer ser = new JSONSerializer();
		//ser.include("CabinJson", "CabinJson.guests") //, "dateTo", "dateFrom", "guests", "termsAndConditions", "guests.id", "guests.ageRange", "guests.guestType", "guests.nr", "guests.price", "guests.isMember")
		//.exclude("*.class")
		//.transform(new DateTimeTransfomer2(), DateTime.class);
		
		CabinJson cj = new CabinJson((long)1, RDate.fDt.plusWeeks(1), RDate.fDt.plusWeeks(2), guests, false);
		System.out.println("CABINJSON TO STRING SAYS! "+cj);
		return ser.serialize(cj);
	}
	
	/**
	 * Same as above but with terms and conditions false
	 *
	private static String getInvalidCabinJson(ArrayList<GuestJson> guests) {
		JSONSerializer ser = new JSONSerializer();
		//ser.include("CabinJson", "CabinJson.guests") //, "dateTo", "dateFrom", "guests", "termsAndConditions", "guests.id", "guests.ageRange", "guests.guestType", "guests.nr", "guests.price", "guests.isMember")
		//.exclude("*.class")
		//.transform(new DateTimeTransfomer2(), DateTime.class);
		
		CabinJson cj = new CabinJson((long)1, RDate.fDt.plusWeeks(1), RDate.fDt.plusWeeks(2), guests, false);
		System.out.println("CABINJSON TO STRING SAYS! "+cj);
		ObjectNode node = Json.newObject();
		return ser.serialize(cj);
	}
*/
	//example of a faulty json to be tested, similar methods to be created
	public static String getOnlyMemberBabiesBookingJSON() {
		int[] nrOfGuests = {0, 0, 0, 3, 0, 0, 0, 0}; //specify here how many of each type of guest you want
		//since this is a only baby booking, we set the nr of baby guests to 3 (arbitrary number)
		
		ArrayList<GuestJson> guests = GuestJson.addGuests(nrOfGuests);
		String onlyBabiesJson = getValidCabinJSON(guests);
		
		return onlyBabiesJson;
	}
	
	public static String getOkBooking() {
		int[] nrOfGuests = {2, 0, 0, 0, 0, 0, 0, 0}; //2 member adults, OK
		ArrayList<GuestJson> guests = GuestJson.addGuests(nrOfGuests);
		String okBooking = getValidCabinJSON(guests);
		
		System.out.println("RETURNING OK BOOKING with SIZE: "+okBooking.length());
		
		return okBooking; 
	}
	
	/*public static JsonNode getInvalidCabinBooking() {
		int[] nrOfGuests = {2, 0, 0, 0, 0, 0, 0, 0}; //2 member adults, OK
		ArrayList<GuestJson> guests = GuestJson.addGuests(nrOfGuests);
		JsonNode invalidBooking = getInvalidCabinJson(guests);
		
		return invalidBooking; 
	}*/
	//INSERT MORE METHODS PRODUCING DIFFERENT JSONS HERE
	
	//TODO method getOnlyNonMemberBabies
	
	//TODO method getNoGuests, guest nr all 0
	
}