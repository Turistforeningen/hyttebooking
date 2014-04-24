package controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.fasterxml.jackson.databind.node.ObjectNode;

import flexjson.JSONSerializer;
import models.Bed;
import models.Booking;
import models.Cabin;
import models.LargeCabin;
import models.SmallCabin;
import play.i18n.Messages;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.With;
import scala.concurrent.duration.Duration;
import utilities.BookingForm;
import utilities.DateTimeTransformer;
import utilities.JsonMessage;
import utilities.Page;

public class BookingController extends Controller {

	/**
	 * Method for retrieving availability for a time period. Returns an int array [n, m, ..., z] where
	 * n represents number of beds available at given date. The number of days between given time period decides the size of the array
	 * So startDate 1. April and endDate 5. April gives array [0, 0, 0, 0, 0] for a cabin with zero beds available.
	 * For small cabins the entire cabin is treated as one bed, so 1 is available and 0 is not available.
	 */
	public static Result getAvailabilityForTimePeriod(long cabinId) {
		ObjectNode result = Json.newObject();

		String from = request().getQueryString("startDate");
		String to = request().getQueryString("endDate");

		if(from == null || to == null)
			return badRequest();

		DateTime startDate = utilities.DateHelper.toDt(from); //must be format YYYY-MM-DD standard ISO date
		DateTime endDate = utilities.DateHelper.toDt(to); //must be format YYYY-MM-DD standard ISO date

		//long cabinId = json.get("cabinId").asLong(); passing as parameter instead

		int[] bookedDays = new int[Math.abs(Days.daysBetween(startDate, endDate).getDays())+1];
		JSONSerializer serializer = new JSONSerializer();

		Cabin cabin = Cabin.find.byId(cabinId);
		if(cabin instanceof LargeCabin) {
			for(Bed beds : ((LargeCabin) cabin).beds) {
				for(Booking b : beds.bookings) {
					if(b.status<Booking.CANCELLED) { //if booking isn't cancelled or timedout
						int[] indices = utilities.DateHelper.getIndex(startDate, new DateTime(b.dateFrom), new DateTime(b.dateTo));
						if(indices[0] < 0) //if b.dateFrom precedes startDate, skip to startDate 
							indices[0] = 0;
						if(indices[1] > bookedDays.length) //if b.dateTo extends beyond endDate we set the last index of the daterange to be plussed
							indices[1] = bookedDays.length-1;
						for(int i = indices[0]; i < indices[1]; i++) { //setting i < indices[1] because the last day is the exit day, and therefore isn't actually booked
							bookedDays[i] += 1; //blankets daterange with +1 to indicate that 1 extra bed is taken during that period
						}
					}
				}
			}
			result.put("bookedDays", serializer.serialize(bookedDays));
			return ok(result);
		}
		else if(cabin instanceof SmallCabin) {
			List<Booking> bookings = SmallCabin.findAllBookingsForCabinGivenDate(cabinId, startDate, endDate);

			if(!bookings.isEmpty()) {
				for(Booking b: bookings) {
					//for each booking set bookedDays[i] = +1 for range startDate-endDate
					int[] indices = utilities.DateHelper.getIndex(startDate, new DateTime(b.dateFrom), new DateTime(b.dateTo)); /** indices[0] startIndex in bookedDays, [1] is endIndex **/
					if(indices[0] < 0) //if b.dateFrom precedes startDate, we set startDate as the first index of the daterange to be set as true (1) 
						indices[0] = 0;
					if(indices[1] > bookedDays.length) //if b.dateTo extends beyond endDate we set the last index of the daterange to be set as true (1)
						indices[1] = bookedDays.length-1;
					for(int i = indices[0]; i < indices[1]; i++){ //setting i < indices[1] because the last day is the exit day, and therefore isn't actually booked
						bookedDays[i] = 1;						}
				}
				result.put("bookedDays", serializer.serialize(bookedDays));
				return ok(result);
			} else { //Either something is wrong or the entire given daterange shows available for given cabin
				result.put("bookedDays", serializer.serialize(bookedDays));
				return ok(result);	
			}
		} else {
			result.put("status", "KO");
			result.put("message", Messages.get("date.invalid"));
			return badRequest(result);
		}
	}


	/**
	 * Controller method that use a the custom subclass bookingForm to 
	 * validate and bind the json data in the request body.
	 * If there is any validation errors the isValid method will return false.
	 * 
	 * 
	 * @return json string describing what went wrong. Validation or booked to capacity etc
	 */
	@With(SecurityController.class)
	public static Result submitBooking() {

		BookingForm form = BookingForm
				.deserializeJson(request().body().asJson().toString());
		if(form.isValid()) {
			//already saved by model helper method inside
			Booking booking = form.createModel();

			if(booking == null) {
				return badRequest(form.getError());
			}
			else {
				//maybe pass a message to a dedicated actor instead of runnable.
				//TODO: Read akka documentation more carefully
				final Long id = booking.id;
				Akka.system().scheduler().scheduleOnce(Duration.create(30, TimeUnit.MINUTES),
						new Runnable() {
					@Override
					public void run() {
						Booking b = Booking.getBookingById(id+"");
						if(!b.status.equals(Booking.PAID)) {
							System.out.println("Oh no your didnt!");
							//cancel booking and unlock beds or cabin for other customers
							//What happens if customer leaves for half an hour and comes
							//back and finishes payment? QUETION
							//remember that timeout should be also considered in isAvail methods
							b.status = Booking.TIMEDOUT;
							b.update();

						}
					}
				}, Akka.system().dispatcher());

				ObjectNode response = form.getSuccess();
				response.put("id", booking.id +"");
				return ok(form.getSuccess());
			}
		}
		else {
			return badRequest(form.getError());
		}
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
	@With(SecurityController.class)
	public static Result cancelBooking(String bookingId) {
		Booking booking = Booking.getBookingById(bookingId);

		if(booking == null) {
			return notFound(JsonMessage.error(Messages.get("booking.notFound")+ ": " + bookingId));
		}

		if(booking.user.id != SecurityController.getUser().id) {
			return badRequest(JsonMessage.error(Messages.get("booking.noAccess")));
		}

		if(!booking.isAbleToCancel()) {
			return notFound(JsonMessage.error(Messages.get("booking.notFound")));
		}

		if(booking.user.id != SecurityController.getUser().id) {
			return badRequest(JsonMessage.error(Messages.get("booking.noAccess")));
		}

		if(!booking.isAbleToCancel()) {
			return badRequest(JsonMessage.error(Messages.get("booking.cannotCancel")));
		}
		//cancellogic to late to cancel?
		booking.status = Booking.CANCELLED;
		PaymentController.cancelPayment(booking.payment.getTransactionId());

		booking.update();


		booking.update();		
		return ok(JsonMessage.success(""));
	}

	/** 
	 * Gets priceMatrix from LargeCabin and serializes it. If smallCabin returns 
	 * memberPrice and nonMemberPrice
	 */
	public static Result getPriceForCabin(Long id) {
		/*
		ObjectNode result = Json.newObject();

		Cabin cabin = Cabin.find.byId(id);

		if(cabin instanceof LargeCabin) {
			JSONSerializer priceSerializer = new JSONSerializer()
			.include()
			.exclude("*.class");
			return Results.ok(priceSerializer.serialize(((LargeCabin) cabin).priceMatrix));
		} else if(cabin instanceof SmallCabin) {
			JSONSerializer priceSerializer = new JSONSerializer()
			.include()
			.exclude("*.class");
			return TODO; //TODO 
		} else {
			result.put("Status", "KO");
			result.put("message", "To late to cancel");
			return badRequest(result);
		}
		 */


		/** DELETE AFTER debugging getPriceForCabin **/
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
		/***/
	}

	/**
	 * Extract optional page-parameter to obtain page variable, and
	 * gets a page of the current user's (authenticated by securitycontroller),
	 * order-history. The bookings are serialized to a json string.
	 * @return Json with a page of orderHistory
	 */
	@With(SecurityController.class)
	public static Result getOrderHistory() {

		//If query parameters page or size not present, default values will be used
		int page = Page.pageHelper(request().getQueryString("page"));
		int pageSize = Page.pageSizeHelper(request().getQueryString("size"));

		Page<Booking> bookings = Booking.getBookingPageByUser(SecurityController.getUser(), page, pageSize);

		JSONSerializer orderDetailsSerializer = new JSONSerializer()
		.include("data", "data.cabin" )
		.exclude("*.class", "data.beds", "data.user", "data.smallCabin", "data.cabin.type", "data.cabin.nrOfBeds", "data.cabin.nrBookings"
				, "data.cabin.cabinUrl", "data.cabin.nrOfBookings", "data.deliveryDate")
				 .transform(new DateTimeTransformer(), DateTime.class);
		return Results.ok(orderDetailsSerializer.serialize(bookings));
	}

}
