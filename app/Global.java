import play.*;
import play.libs.*;

import com.avaje.ebean.Ebean;

import models.*;

import java.util.*;

/**
 * Dummy generation test
 * v0.1 (Jama) Changes made:
 * 	1. Almost the same functionality, just removed names. Numbering cabins from now on, that way we keep integrity 
 * with model (since cabins have no name [debatable])
 */
public class Global extends GlobalSettings {
	@Override
	public void onStart(Application app) {

		// Populate database with dummy data
		
	    
		if (User.find.findRowCount() == 0) {
			User user1 = new User("olavvatne@gmail.com", "p", "John Doe");
			user1.save();
			User user2 = new User("user2@demo.com", "password", "Jane Doe");
			user2.save();
		    User[] us = {user1, user2}; 
		    new Guest(user1.id).save();
		    new Guest(user2.id).save();
		    
		   Cabin[] cabins = {new SmallCabin("Helfjord"), new LargeCabin("Fjordlistølen")};
		   cabins[0].save();
		   cabins[1].save();
		   
			for ( int i = 0; i<40; i++) {
				new Booking((long)i, new Date(), new Date(), cabins[i%2].id ).save();
				
				
				
				
//models.Booking.Booking(String userId, Date dayOfBookingStart, Date dayOfBookingEnd, SmallCabin cabin)
			}
		}
		
	}
}
