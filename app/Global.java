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
	public final static int NR_OF_DUMMY_BOOKINGS = 2;
	public final static int BOOKING_DATE_RANGE = 40;
	@Override
	public void onStart(Application app) {

		// Populate database with dummy data
		
	    
		if (User.find.findRowCount() == 0 && !app.isTest()) {
			User user1 = new User("q", "w", "John Doe");
			user1.save();
			User user2 = new User("w", "p", "Jane Doe");
			user2.save();
			User admin = new User("admin", "p", "admin");
			admin.admin = true;
			admin.save();
			
		    User[] us = {user1, user2};
		    int userSize = us.length;
		    
		   LargeCabin lc1 = new LargeCabin("Fjordheim", 10);
		   lc1.save();
		   
		   LargeCabin lc2 = new LargeCabin("Peterstun", 20);
		   lc2.save();
		   
		   SmallCabin sc1 = new SmallCabin("Helfjord");
		   sc1.save();
		   
		   SmallCabin sc2 = new SmallCabin("Fjordlistølen");
		   sc2.save();
		   
		   Cabin[] cabins = {lc1,sc1 ,sc2 , lc2};
		   
		   
		   	int cabinSize = cabins.length;
			for ( int i = 0; i<NR_OF_DUMMY_BOOKINGS; i++) {
				Random r = new Random();
				
				
				//Booking -20 to 20 days in the future from today
				int fromDays = -BOOKING_DATE_RANGE/2 +(int)(Math.random()*BOOKING_DATE_RANGE);
				//booking 1 -5 days + fromdays in the future from today
				int toDays;
				if(fromDays > 0) {
					toDays = fromDays+ 1 +(int)(Math.random()*5);
				}
				else {
					toDays =1 +(int)(Math.random()*5);
				}
				
				//bookingDate 1-20 days before fromdate
				int bookingDays = fromDays +(int)(Math.random()*BOOKING_DATE_RANGE/2);
				
				Date fromDate = DateTime.now().plusDays(fromDays).toDate();
				Date toDate = DateTime.now().plusDays(toDays).toDate();
				Date bookingDate = DateTime.now().minusDays(bookingDays).toDate();
				
				List<Bed> beds = null;
				Cabin currentCabin = cabins[r.nextInt(cabinSize)];
				
				if(currentCabin instanceof LargeCabin) {
					beds = ((LargeCabin)currentCabin).beds;
					
				}
				else {
					beds =null;
				}
				
				Booking b= Booking.createBooking(us[i%userSize].id, fromDate, toDate, currentCabin.id, beds );
				b.timeOfBooking = bookingDate.getTime();
				b.update();
				
			
			}
		}
		
	}
}
