package controllers;

import java.util.List;


import models.Booking;
import play.*;
import play.libs.Json;
import play.db.ebean.Model;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {	
    
    public static Result bookAtCabin(String cabinID) {
    	String beds = request().getQueryString("sengeplasser");
    	String bookingType = request().getQueryString("booking");
    	//gets the parameters, sengeplasser and booking when redirected from dnt.
    	System.out.println("cabinId: " + cabinID + " ,beds " + beds + " ,type: " + bookingType);
    	//If cabinID dont match any cabin in database return fail.
    	
    	
    	return TODO;
    }
    

}
