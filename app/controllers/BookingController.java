package controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;




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
	
	/**
	 * TODO document this
	 * @return
	 */
	public static Result getAvailabilityForTimePeriod() {
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
			
			//bookedDays[n] = true if and only if date is booked i.e. not available
			boolean[] smallCBookedDays = new boolean[Math.abs(Days.daysBetween(startDate, endDate).getDays())+1];
			int[] largeCBookedDays = new int[Math.abs(Days.daysBetween(startDate, endDate).getDays())+1];
			JSONSerializer serializer = new JSONSerializer();
			
			Cabin cabin = Cabin.find.byId(cabinId);
			if(cabin instanceof LargeCabin) {
				//TODO implement
				// use LargeCabin.book method
				return TODO;
			} else if(cabin instanceof SmallCabin) {
				List<Booking> bookings = cabin.findAllBookingsForCabinGivenDate(cabinId, startDate, endDate);
				
				if(!bookings.isEmpty()) {
					for(Booking b: bookings) {
						//for each booking set bookedDays[i] = true for range startDate-endDate
						int[] indices = utilities.DateHelper.getIndex(startDate, new DateTime(b.dateFrom), new DateTime(b.dateTo)); /** indices[0] startIndex in bookedDays, [1] is endIndex **/
						if(indices[0] < 0) //if b.dateFrom precedes startDate, skip to startDate 
							indices[0] = 0;
						for(int i = indices[0]; i<=indices[1]; i++){
							smallCBookedDays[i] = true; //TODO test
						}
					}
					result.put("bookedDays", serializer.serialize(smallCBookedDays));
					return ok(result);
				} else { //Either something is wrong or the entire given daterange shows available for given cabin
					result.put("bookedDays", serializer.serialize(smallCBookedDays));
					return ok(result);	
				}
			} else {
				result.put("status", "KO");
				result.put("message", "date invalid");
				return badRequest(result);
			}
		}
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

			//TODO should be some sort of check here that booking != null
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
    
	public static Result getPriceForCabin(Long id) {
		
		class ListItem implements Serializable {
            public int nr;
            public String type;
            public int price;
            
            public ListItem(int nr, String type, int price) {
            	this.nr = nr;
            	this.type = type;
            	this.price = price;
            }
        }
		
		Cabin cabin = Cabin.find.byId(id);
		if(cabin == null) {
			return Results.badRequest();
		}
		
		List<ListItem> list = new ArrayList<ListItem>();
		if(cabin instanceof LargeCabin) {
			list.add(new ListItem(0, "Voksen, medlem", 300));
			list.add(new ListItem(0, "Ungdom, medlem", 150));
			list.add(new ListItem(0, "Barn, medlem", 100));
			list.add(new ListItem(0, "Spedbarn", 0));
			list.add(new ListItem(0, "Voksen", 400));
			list.add(new ListItem(0, "ungdom", 200));
			list.add(new ListItem(0, "barn", 150));
			JSONSerializer priceSerializer = new JSONSerializer()
			.include()
			.exclude("*.class");
			return Results.ok(priceSerializer.serialize(list));
		}
		else {
			list.add(new ListItem(1, "Hele", 1000));
			JSONSerializer priceSerializer = new JSONSerializer()
			.include()
			.exclude("*.class");
			return Results.ok(priceSerializer.serialize(list));
		}
		
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