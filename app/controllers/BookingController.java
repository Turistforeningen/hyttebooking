package controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import flexjson.JSONSerializer;
import models.Booking;
import models.Cabin;
import models.Guest;
import models.LargeCabin;
import models.Page;
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
		 * Input: startDate, Enddate, NrOfPerson, CabinID (if partial booking allowed)
		 * 
		 * When choosing a time period and persons availability for
		 * each date should be returned as JSON to be dynamically
		 * displayed on client 
		 * 
		 *returns a json string with a boolean for each date.
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
			DateTime startDt = dateHelper(start);
			System.out.println("Start dt: "+startDt);
			String end = json.get("dayOfBookingEnd").asText();
			DateTime endDt = dateHelper(end);
			System.out.println("End dt: "+endDt);
			
			//validate request here
			if(nrPerson 	!= null &&
					startDt != null &&
					endDt 	!= null &&
					!startDt.isBeforeNow() &&
					!endDt.isBeforeNow() &&
					startDt.isBefore(endDt)
					) {
				//TESTLINE
				Booking booking = new Booking(
						SecurityController.getUser().id, 
						startDt.toDate(),
						endDt.toDate(),
						tempCabin.id,
						null);
				booking.save();
				result.put("status", "OK");
				result.put("message", "booking saved");
				return ok(result);
			}
			else {
				result.put("status", "KO");
				result.put("message", "date invalid");
				return badRequest(result);
			}
		}
	}

	/** Helper method for dateTime object from string "dd-MM-YYYY" **/
	public static DateTime dateHelper(String date) {
		
		String[] d = date.split("-");
		if(d.length < 3) return null;
		DateTime dt = new DateTime(Integer.parseInt(d[0]), //int year
				Integer.parseInt(d[1]), //int month
				Integer.parseInt(d[2]), //int day
				0, 						//int hour
				0						//int minute
				);
		return dt;
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
    	if(booking.payment.user.id != SecurityController.getUser().id) {
    		return badRequest("No access");
    	}
    	
    	
    	booking.delete();
    	return ok(Json.toJson(booking));
    	
    }
    
    /**
     * Extract optional pageparamter to obtain page variable, and
     * gets a page of the current user's (authenticated by securitycontroller),
     * orderhistory. The bookings are serialized to a json string.
     * @return Json with a page of orderHistory
     */
	public static Result getOrderHistory() {
		
		int pageSize = 5;
		int page = 0;
		try {
			page = Integer.parseInt(request().getQueryString("page"));
		} catch (Exception e) {
			page = 0;
		}
		
		Page bookings = Booking.getBookingPageByUser(SecurityController.getUser(), page, pageSize);
		JSONSerializer orderDetailsSerializer = new JSONSerializer().include("orders", "orders.cabin" ).exclude("*.class", "beds", "smallCabin");
		return Results.ok(orderDetailsSerializer.serialize(bookings));
	}
}
