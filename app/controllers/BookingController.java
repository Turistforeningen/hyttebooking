package controllers;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import models.Booking;
import play.api.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;


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
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest("Expected Json data");
		}
		else {
			System.out.println(json.asText());
			return ok("Saved to database");
		}
		
	}
	
	
	public static Result cancelBooking(String bookingID) {
    	//Perform business logic - cancel before x days etc
    	//If cancel - refund through nets, fully or partially
    	//If to late to cancel - return fail. 
    	//(Frontend should prevent this user error also from happening)
    	//Update database with new availability for given cabin.
    	Booking booking = Booking.find.where().eq("id", bookingID).findUnique();
    	if(booking != null) {
    		booking.delete();
    		return ok(Json.toJson(booking));
    	}
    	else {
    		return notFound();
    	}
    }
    
    
    public static Result getOrderHistory(String userID) {
    	List<Booking> bookings = Booking.find.where().eq("userId", userID).findList();
    	//How to get userID (solve login with DNT Connect?
    	//How to handle unregistrered users?
    	
    	//Method should return a template or Json, with current and past bookings.
    	return Results.ok(Json.toJson(bookings));
    }
}
