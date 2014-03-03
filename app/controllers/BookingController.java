package controllers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Booking;
import models.Cabin;
import models.Guest;
import models.User;
import play.api.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.With;

@With(SecurityController.class)
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
		ObjectNode result = Json.newObject();
		JsonNode json = request().body().asJson();
		if(json == null) {
			result.put("status", "KO");
			result.put("message", "Expected Json");
			return badRequest(result);
		}
		else {
			Cabin tempCabin = Cabin.find.where().eq("name", "Helfjord").findUnique();
			String nrPerson = json.get("nrOfPersons").asText();
			String start = json.get("dayOfBookingStart").asText();
			String end = json.get("dayOfBookingEnd").asText();
			//validate request here
			if(nrPerson 	!= null &&
					start 	!= null &&
					end 	!= null
					) {
				//TESTLINE
				Booking booking = new Booking(
						SecurityController.getUser().id, 
						new Date(start),
						new Date(end),
						tempCabin.id);
				booking.save();
				result.put("status", "OK");
				result.put("message", "booking saved");
				return ok(result);
			}
			else {
				return badRequest();
			}
			
		}
		
	}
	
	
	public static Result cancelBooking(String bookingID) {
    	//Perform business logic - cancel before x days etc
    	//If cancel - refund through nets, fully or partially
    	//If to late to cancel - return fail. 
    	//(Frontend should prevent this user error also from happening)
    	//Update database with new availability for given cabin.
    	Booking booking = Booking.find.where().eq("id", bookingID).findUnique();
    	if(booking == null) {
    		return notFound();
    	}
    	if(booking.guest.id != SecurityController.getUser().id) {
    		return badRequest("No access");
    	}
    	
    	
    	booking.delete();
    	return ok(Json.toJson(booking));
    	
    }
    
    
    public static Result getOrderHistory() {
    	
    	//List<Booking> bookings = Booking.findBookingByUser(SecurityController.getUser().id);
    	 Guest user = Guest.find.where().eq("id", SecurityController.getUser().id).findUnique();
    	 List<Booking> bookings = user.booking;
    	return Results.ok(Json.toJson(bookings));
    }
}
