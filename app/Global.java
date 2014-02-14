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
			String[] cabins = {"Helfjord", "Bergene", "Fjørlistølen", "Trollkapp" };
			for ( int i = 0; i<40; i++) {
				
				new Booking(new Date(114, 03, 23), ((i%6)+Math.round(Math.random()*2)) +"",new Date(2014, 04, 3 + i),new Date(114, 04, 10 + i), cabins[i%cabins.length]).save();
			}
		}
	}
}
