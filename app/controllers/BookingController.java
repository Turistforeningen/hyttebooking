package controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import flexjson.JSONSerializer;
import models.Bed;
import models.Booking;
import models.Cabin;
import models.Guest;
import models.LargeCabin;
import models.SmallCabin;
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
		ObjectNode result = Json.newObject();
		JsonNode json = request().body().asJson();
		if(json == null) {
			result.put("status", "KO");
			result.put("message", "Expected Json");
			return badRequest(result);
		}
		else {
			//startDate, endDate, nrOfPerson, cabinId
			DateTime startDate = utilities.DateHelper.toDt(json.get("startDate").asText()); //must be format YYYY-MM-DD standard ISO date
			DateTime endDate = utilities.DateHelper.toDt(json.get("endDate").asText()); //must be format YYYY-MM-DD standard ISO date
			int nrOfPerson = json.get("nrOfPerson").asInt();
			long cabinId = json.get("cabinId").asLong(); 
			//dynamic programming, will fill this boolean according too all bookings
			boolean[] bookedDays = new boolean[utilities.DateHelper.daysBetween(startDate, endDate)];
			
			Cabin cabin = Cabin.find.byId(cabinId);
			if(cabin instanceof LargeCabin) {
				//TODO implement
				//a bit different from smallCabin. Will implement later @author jama
			} else if(cabin instanceof SmallCabin) {
				List<Booking> bookings = cabin.findAllBookingsForCabinGivenDate(cabinId, startDate, endDate);
				
				if(!bookings.isEmpty()) {
					//Use JSONSerializer TODO
					for(Booking b: bookings) {
						//for each booking
						
						//find index b.fromDate corresponds to in availableDays array, call it fromIndex
						//set true from fromIndex to b.toDate's toIndex
						//TODO
					}
					//return bookedDays as json TODO
				} else { //Either something is wrong or the entire given daterange shows available for given cabin
					
					
					//return bookedDays as json TODO
				}
			}
		}
		return TODO;
	}
	
	private static List addBooleanBlock(int daysBetween) {
		ArrayList<Boolean> arr = new ArrayList<Boolean>();
		for(int i = 0; i<daysBetween; i++)
			arr.add(true);
		
		return arr;
	}
	
	public static Result submitBooking() {
		ObjectNode result = Json.newObject();
		JsonNode json = request().body().asJson();
		if(json == null) {
			result.put("status", "KO");
			result.put("message", "Expected Json");
			return badRequest(result);
		}
		
		Cabin cabin = Cabin.find.byId(json.get("cabinId").asLong());
		if(cabin == null) {
			result.put("status", "KO");
			result.put("message", "cant book at this cabin");
		}
		
		String nrPerson = json.get("beds").asText();
		String start = json.get("dateFrom").asText();
		System.out.println(json.get("dateFrom").asText());
		DateTime startDt = utilities.DateHelper.toDt(start);
		System.out.println("Start dt: "+startDt);
		String end = json.get("dateTo").asText();
		DateTime endDt = utilities.DateHelper.toDt(end);
		System.out.println("End dt: "+endDt);

		//validate request here
		if(
				startDt == null ||
				endDt 	== null ||
				startDt.isBeforeNow() ||
				endDt.isBeforeNow() ||
				!startDt.isBefore(endDt)
				) 
		{
			result.put("status", "KO");
			result.put("message", "date invalid");
			return badRequest(result);
		}
			
			List<Bed> beds = null;
			if (cabin instanceof LargeCabin) {
				beds = ((LargeCabin) cabin).book(Integer.parseInt(nrPerson), startDt, endDt);
				if(beds == null) {
					result.put("status", "KO");
					result.put("message", "no available beds");
					return badRequest(result);
				}
			}
			
			Booking booking = Booking.createBooking(
					SecurityController.getUser().id, 
					startDt.toDate(),
					endDt.toDate(),
					cabin.id,
					beds);

			result.put("status", "OK");
			result.put("message", "booking saved");
			return ok(result);

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
		
		
		Page<Booking> bookings = Booking.getBookingPageByUser(SecurityController.getUser(), page, pageSize);
		JSONSerializer orderDetailsSerializer = new JSONSerializer()
				.include("data", "data.cabin" )
				.exclude("*.class", "beds", "data.smallCabin", "data.cabin.type", "data.cabin.nrOfBeds", "data.cabin.nrBookings");
		return Results.ok(orderDetailsSerializer.serialize(bookings));
	}
}