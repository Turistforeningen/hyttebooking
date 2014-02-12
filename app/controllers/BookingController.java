package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class BookingController extends Controller {
	
	public static Result getAvailabilityForTimePeriod() {
		/* PROPOSAL
		 * Input: startDate, Enddate, NrOfPerson (if partial booking allowed)
		 * 
		 * When choosing a time period and persons availability for
		 * each date should be returned as JSON to be dynamically
		 * displayed on client 
		 * 
		 */
		return TODO;
	}
	
	public static Result submitBooking() {
		//Validate user input
		//Validate cabin booking date time period. 
		//If error return fail
		//If sucess -> return sucess, 
		return TODO;
	}
}
