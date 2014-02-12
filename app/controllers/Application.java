package controllers;

import java.util.List;


import models.Booking;
import play.*;
import play.libs.Json;
import play.db.ebean.Model;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(indexAngular.render());
    }
    
    public static Result bookAtCabin(String cabinID) {
    	//If cabinID dont match any cabin in database return fail.
    	
    	return TODO;
    }
    
    public static Result cancelBooking(String bookingID) {
    	//Perform business logic - cancel before x days etc
    	//If cancel - refund through nets, fully or partially
    	//If to late to cancel - return fail. 
    	//(Frontend should prevent this user error also from happening)
    	//Update database with new availability for given cabin.
    	Booking booking = Booking.find.where().eq("id", bookingID).findUnique();
    	booking.delete();
    	return TODO;
    }
    
   
    
    public static Result getOrderHistory(String userID) {
    	List<Booking> bookings = Booking.find.where().eq("userId", userID).findList();
    	//How to get userID (solve login with DNT Connect?
    	//How to handle unregistrered users?
    	
    	//Method should return a template or Json, with current and past bookings.
    	return Results.ok(Json.toJson(bookings));
    }
    
    
}
