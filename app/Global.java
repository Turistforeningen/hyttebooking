import play.*;
import play.libs.*;

import com.avaje.ebean.Ebean;

import models.*;

import java.util.*;

import org.hibernate.validator.constraints.Length;

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
		    new Guest(user1.id).save();
		    new Guest(user2.id).save();
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
					System.out.println(beds.size());
				}
				Booking b= new Booking((long)i%userSize, new Date(), new Date(), cabins[i%cabinSize].id, beds );
				b.save();
				
			}
		}
		
	}
}
