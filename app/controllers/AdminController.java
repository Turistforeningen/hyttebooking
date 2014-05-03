package controllers;



import java.util.HashMap;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Booking;
import models.Cabin;
import models.LargeCabin;
import models.SmallCabin;
import flexjson.JSONSerializer;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.With;
import utilities.CabinForm;
import utilities.DateTimeTransformer;
import utilities.JsonMessage;
import utilities.Page;

@With(SecurityController.class)
public class AdminController extends Controller {
	
	/**
	 * Controller method can only be accessed by users with
	 * admin privileges. Returns a list with cabins in booking sys
	 * Must specify page and pageSize queryParameters in route-url
	 * 
	 * @return Result containing cabins
	 */
	public static Result getCabins() {
		if (!SecurityController.getUser().admin) {
			return unauthorized();
		}
		
		int page = Page.pageHelper(request().getQueryString("page"));
		int pageSize = Page.pageSizeHelper(request().getQueryString("size"));
		
		Page<Cabin> cabins = Cabin.findAllCabins(page, pageSize); 
		JSONSerializer cabinSerializer = new JSONSerializer().include("data").exclude("*.class");
		
		return ok(cabinSerializer.serialize(cabins));
	}
	
	//TODO: Possibly split into getCabin and getCabinBookings ... rest: /api/cabins/:id/bookings and /api/cabins/:id
	//what is more efficient
	/**
	 * Controller method can only be accessed by users with
	 * admin privileges. Finds all bookings for a given cabin
	 * @param id - cabinId
	 * @return Result containing bookings for a given cabin
	 */
	public static Result getCabinDetails(Long id) {
		if (!SecurityController.getUser().admin) {
			return unauthorized();
		}
		
		int page = Page.pageHelper(request().getQueryString("page"));
		int pageSize = Page.pageSizeHelper(request().getQueryString("size"));
		 System.out.println(pageSize + " ------------------");
		Page<Booking> bookingsAtCabin = Cabin.findAllBookingsForCabin(id, page, pageSize);
		Cabin cabin = Cabin.find.byId(id);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("cabin", cabin);
		map.put("bookingList", bookingsAtCabin);
		JSONSerializer bookingSerializer = new JSONSerializer()
		.include("data" )
		.exclude("*.class", "bookingList.data.smallCabin", "bookingList.data.cabin")
		.transform(new DateTimeTransformer(), DateTime.class);
		
		return ok(bookingSerializer.serialize(map));
	}
	
	
	/**
	 * A cabin can be added to the booking system by running submitCabin.
	 * This is naturally a restricted operation only for admins.
	 * 
	 * The json request are validated and bound to a model using the CabinForm.
	 * A subclass of AbstractForm. The data are deserialized and validated, and 
	 * if errors are found a error message is created by the form which this
	 * method returns.
	 * @return a Result containing a json string with status and message value.
	 */
	public static Result submitCabin() {
		
		if (!SecurityController.getUser().admin) {
			return unauthorized();
		}
		
		CabinForm form = utilities.CabinForm
				.deserializeJson(request().body().asJson().toString());
		
		if(form.isValid()) {
			
			Cabin c =form.createModel();
			
			if (c == null) {
				return badRequest(form.getError());
			}
			else {
				return ok();
			}
		}
		else {
			return badRequest(form.getError());
		}
	}
	
	@With(SecurityController.class)
	public static Result adminCancelBooking(String bookingId) {
		Booking booking = Booking.getBookingById(bookingId);

		if(booking == null) {
			return notFound(JsonMessage.error(Messages.get("booking.notFound")+ ": " + bookingId));
		}

		if(!SecurityController.getUser().admin) {
			return badRequest(JsonMessage.error(Messages.get("admin.noAccess")));
		}

		//7 days before. Admin should still be able to cancel then
		if(!booking.isAbleToCancel()) {
			return notFound(JsonMessage.error(Messages.get("booking.notFound")));
		}

		//cancellogic to late to cancel?
		
		if(booking.status == Booking.PAID) {
			PaymentController.cancelPayment(booking.payment.getTransactionId());
		}
		booking.status = Booking.CANCELLED;
		booking.update();	
		return ok(JsonMessage.success(""));
	}
}
