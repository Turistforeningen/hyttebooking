package controllers;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

public class BookingJson {
	/* EXAMPLE
	 * "cabinId":"1",
   "dateTo":"2014-05-22",
   "dateFrom":"2014-05-15",
   "guests":[
	 */
	
	public long cabinId;
	public DateTime dateTo;
	public DateTime dateFrom;
	public List<GuestJson> guests = new ArrayList<GuestJson>();
	public boolean termsAndConditions;
	
	public BookingJson(long cabinId, DateTime dateTo, DateTime dateFrom, List<GuestJson> gJson, boolean termsAndConditions) {
		this.cabinId = cabinId;
		this.dateTo = dateTo;
		this.dateFrom = dateFrom;
		this.guests.addAll(gJson);
		this.termsAndConditions = termsAndConditions;
	}
	
	public String toString() {
		return ""+cabinId+", "+DateTimeTransfomer2.dtToStringHyphenated(dateTo)+", "+
				DateTimeTransfomer2.dtToStringHyphenated(dateFrom)+", "+
				"guests ["+guests+"], "+
				termsAndConditions;
	}
}
