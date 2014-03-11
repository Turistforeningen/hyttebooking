import play.*;
import play.libs.*;

import com.avaje.ebean.Ebean;

import models.*;

import java.util.*;

import org.hibernate.validator.constraints.Length;
import org.joda.time.DateTime;

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
			User user1 = new User("q", "w", "John Doe");
			user1.save();
			User user2 = new User("user2@demo.com", "password", "Jane Doe");
			user2.save();
			
		    User[] us = {user1, user2};
		    int userSize = us.length;
		    
		   LargeCabin lc = new LargeCabin("Fjordheim", 10);
		   lc.save();
		   
		   LargeCabin lc2 = new LargeCabin("Peterstun", 20);
		   lc2.save();
		   
		   Cabin[] cabins = {lc, new SmallCabin("Helfjord"), new SmallCabin("Fjordlistølen"), lc2};
		   
		   cabins[1].save();
		   cabins[2].save();
		   
		   	int cabinSize = cabins.length;
			for ( int i = 0; i<100; i++) {
				List<Bed> beds = null;
				if(i%cabinSize ==0) {
					beds = lc.beds;
				}
				if(i%cabinSize ==3) {
					beds = lc2.beds.subList(0, 1+ (int)Math.floor((Math.random()*15)));
				}
				//Booking -20 to 20 days in the future from today
				int fromDays = -20 +(int)(Math.random()*40);
				//booking 1 -5 days + fromdays in the future from today
				int toDays;
				if(fromDays > 0) {
					toDays = fromDays+ 1 +(int)(Math.random()*5);
				}
				else {
					toDays =1 +(int)(Math.random()*5);
				}
				
				//bookingDate 1-20 days before fromdate
				int bookingDays = fromDays +(int)(Math.random()*20);
				
				Date fromDate = DateTime.now().plusDays(fromDays).toDate();
				Date toDate = DateTime.now().plusDays(toDays).toDate();
				Date bookingDate = DateTime.now().minusDays(bookingDays).toDate();
				
				Booking b= Booking.createBooking(new Long(1+(i%userSize)), fromDate, toDate, cabins[i%cabinSize].id, beds );
				b.timeOfBooking = bookingDate.getTime();
				b.update();
				
			}
		}
		
	}
}
