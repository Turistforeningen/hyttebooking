import play.*;
import play.libs.*;

import com.avaje.ebean.Ebean;

import models.*;

import java.util.*;

public class Global extends GlobalSettings {
	@Override
	public void onStart(Application app) {
		// Populate database with dummy data
		
	    
		if (Booking.find.findRowCount() == 0) {
			User user1 = new User("olavvatne@gmail.com", "p", "John Doe");
			user1.save();
			User user2 = new User("user2@demo.com", "password", "Jane Doe");
			user2.save();
		    User[] us = {user1, user2}; 
			String[] cabins = {"Helfjord", "Bergene", "Fjørlistølen", "Trollkapp" };
			for ( int i = 0; i<40; i++) {
				
				new Booking(new Date(114, 03, 23),  us[i%2],new Date(114, 04, 3 + i),new Date(114, 04, 10 + i), cabins[i%cabins.length]).save();
			}
		}
	}
}
