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
import models.User;
import play.api.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.With;
import utilities.Page;

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
				Booking booking = Booking.createBooking(
						SecurityController.getUser().id, 
						startDt.toDate(),
						endDt.toDate(),
						tempCabin.id,
						null);

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
	
	
	/**
	 * Retrieves booking from database. If no booking match with
	 * bookingID method, return noFound.
	 * Checks if authenticated user is owner of booking, if not,
	 * returns bad request
	 * 
	 * Booking is cancelled by setting flag in booking to cancelled,
	 * not deleted from database.
	 * 
	 * @param bookingID
	 * @return Result response
	 */
	public static Result cancelBooking(String bookingId) {
    	Booking booking = Booking.getBookingById(bookingId);
    	ObjectNode result = Json.newObject();
    	
    	if(booking == null) {
    		result.put("Status", "KO");
    		result.put("message", "No such booking found");
    		return notFound(result);
    	}
    	
    	if(booking.user.id != SecurityController.getUser().id) {
    		result.put("Status", "KO");
    		result.put("message", "No access");
    		return badRequest(result);
    	}
    	
    	if(!booking.isAbleToCancel()) {
    		result.put("Status", "KO");
    		result.put("message", "To late to cancel");
    		return badRequest(result);
    	}
    	//cancellogic to late to cancel?
    	booking.status = Booking.CANCELLED;
    	
    	//repay customer through nets
    	
    	booking.update();
    	result.put("Status", "OK");
		result.put("message", "");
    	return ok(result);
    }
    
	
    /**
     * Extract optional page-parameter to obtain page variable, and
     * gets a page of the current user's (authenticated by securitycontroller),
     * order-history. The bookings are serialized to a json string.
     * @return Json with a page of orderHistory
     */
	public static Result getOrderHistory() {
		
		int page = Page.pageHelper(request().getQueryString("page"));
		int pageSize = Page.pageSizeHelper(request().getQueryString("size"));
		
		
		Page bookings = Booking.getBookingPageByUser(SecurityController.getUser(), page, pageSize);
		JSONSerializer orderDetailsSerializer = new JSONSerializer().include("orders", "orders.cabin" ).exclude("*.class", "beds", "orders.smallCabin");
		return Results.ok(orderDetailsSerializer.serialize(bookings));
	}
}
