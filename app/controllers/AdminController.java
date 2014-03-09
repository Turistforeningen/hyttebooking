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
	 * Must specify page and pageSize queryParameters in routeurl
	 * 
	 * @return Result containing cabins
	 */
	public static Result getCabins() {
		int page = Page.pageHelper(request().getQueryString("page"));
		int pageSize = Page.pageSizeHelper(request().getQueryString("size"));
		
		List<Cabin> cabins = Cabin.find.where()
				.findPagingList(pageSize)
		        .getPage(page).getList();
		JSONSerializer cabinSerializer = new JSONSerializer().include().exclude("*.class");
		
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
		 
		Page bookingsAtCabin = Cabin.findAllBookingsForCabin(id, page, pageSize);
		JSONSerializer bookingSerializer = new JSONSerializer()
		.include("orders", "orders.cabin" )
		.exclude("*.class", "beds", "smallCabin");
		
		return ok(bookingSerializer.serialize(bookingsAtCabin));
	}
}
