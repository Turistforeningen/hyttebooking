package controllers;

import java.util.ArrayList;

public class GuestJson {
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
		if(nrOfGuests.length < JsonHelper.GUEST_TYPE_NAMES.length) {
			System.err.println("ERROR: nrOfGuests array too short!");
			return null;
		}
		ArrayList<GuestJson> guests = new ArrayList<GuestJson>(); 
		for(int i = 0; i<JsonHelper.GUEST_TYPE_NAMES.length; i++) {
			//System.out.println("ADDING: "+1+((long)i%4)+" - "+AGE_RANGE_NAMES[i]+" - "+GUEST_TYPE_NAMES[i]+" - "+nrOfGuests[i]+" - "+GUEST_PRICES[i]+" - "+MEMBERSHIP[i]);
			guests.add(new GuestJson(1+((long)i%4) , JsonHelper.AGE_RANGE_NAMES[i], JsonHelper.GUEST_TYPE_NAMES[i], nrOfGuests[i], JsonHelper.GUEST_PRICES[i], JsonHelper.MEMBERSHIP[i]));
		}
		return guests;
	}
	
	public String toString() {
		return "{"+id+", "+ageRange+", "+guestType+", "+nr+", "+price+", "+isMember+"}";
	}
}
