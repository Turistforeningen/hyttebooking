package controllers;



import models.Booking;
import models.Cabin;

import flexjson.JSONSerializer;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import utilities.CabinForm;
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
		JSONSerializer bookingSerializer = new JSONSerializer()
		.include("data" )
		.exclude("*.class", "data.smallCabin", "data.cabin");
		
		return ok(bookingSerializer.serialize(bookingsAtCabin));
	}
	
	/**
	 * A cabin can be added to the booking system by running submitCabin.
	 * This is naturally a restricted operation only for admins.
	 * 
	 * The json request are validated and binded to a model using the CabinForm.
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
}
