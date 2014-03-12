package controllers;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import models.Booking;
import models.Cabin;
import models.LargeCabin;
import models.SmallCabin;
import models.User;
import flexjson.JSONSerializer;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
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
	
	public static Result submitCabin() {
		
		if (!SecurityController.getUser().admin) {
			return unauthorized();
		}
		
		JsonNode json = request().body().asJson();
		
		String type = json.get("type").asText();
		if(type.equals("SmallCabin")) {
			SmallCabin cabin = new SmallCabin(json.get("name").asText());
			//make convenience methods here, and if id should be set,
			//it must not crash with id of other cabins.
			cabin.save();
			return ok();
		}
		else if(type.equals("LargeCabin")) {
			LargeCabin cabin = new LargeCabin(json.get("name").asText(), json.get("beds").asInt());
			cabin.save();
			return ok();
		}
		return badRequest();
		
	}
}
