package controllers;

import java.util.List;

import models.Booking;
import models.Cabin;
import flexjson.JSONSerializer;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import utilities.Page;

//@With(SecurityController.class)
public class AdminController extends Controller {
	
	/**
	 * Admin functionality
	 * Must specify page and pageSize queryParameters in route-url
	 * 
	 * @return Result containing cabins
	 */
	public static Result getCabins() {
		int page = Page.pageHelper(request().getQueryString("page"));
		int pageSize = Page.pageSizeHelper(request().getQueryString("size"));
		
		Page<Cabin> cabins = Cabin.findAllCabins(page, pageSize); 
		JSONSerializer cabinSerializer = new JSONSerializer().include("data").exclude("*.class");
		
		return ok(cabinSerializer.serialize(cabins));
	}
	
	
	/**
	 * 
	 * @param id - cabinId
	 * @return Result containing bookings for a given cabin
	 */
	public static Result getCabinDetails(Long id) {
		int page = Page.pageHelper(request().getQueryString("page"));
		int pageSize = Page.pageSizeHelper(request().getQueryString("size"));
		 
		Page<Booking> bookingsAtCabin = Cabin.findAllBookingsForCabin(id, page, pageSize);
		JSONSerializer bookingSerializer = new JSONSerializer()
		.include("data" )
		.exclude("*.class", "data.smallCabin", "data.cabin");
		
		return ok(bookingSerializer.serialize(bookingsAtCabin));
	}
}
