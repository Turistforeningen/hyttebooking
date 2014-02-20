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
		if (Booking.find.findRowCount() == 0) {
			for ( int i = 0; i<40; i++) {
				new Booking(((i%6)+Math.round(Math.random()*2)) +"",new Date(2014, 04, 3 + i),new Date(114, 04, 10 + i), new SmallCabin()).save();
			}
		}
	}
}
