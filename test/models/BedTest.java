package models;
import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

import java.util.List;

import models.Bed;
import models.Booking;
import models.Cabin;
import models.LargeCabin;
import models.User;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import play.test.WithApplication;

public class BedTest extends WithApplication{

	/** Create classes to be used in the tests **/
	private LargeCabin c;
	private User u;

	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
		c = new LargeCabin("BedTestModelLargeCabin", 10);
		c.save();
		u = new User("u@u.no", "password1", "BedTestModelUser1");
		u.save();
	}

	@Test
	/**
	 * Tests if isAvailable returns true when it shouldn't:
	 * 	- booking exists in range fromDate-toDate
	 * Tests if isAvailable returns false when it shouldn't: 
	 * 	- booking doesn't exist in range fromDate-toDate 
	 * 	- booking exists but is timedout/cancelled
	 */
	public void testIsAvailable() {

		Long cId = c.id;
		c = (LargeCabin)Cabin.find.byId(cId);
		u = User.find.byId(cId);
		List<Bed> beds = c.beds;

		DateTime from1 = RDate.fDt.plusDays(2);
		DateTime to1 = RDate.fDt.plusDays(10);
		
		DateTime from2 = RDate.fDt.plusMonths(1);
		DateTime to2 = RDate.fDt.plusMonths(1).plusDays(10);

		//booking should save that beds are booked in the dateRange
		Booking b1 = Booking.createBooking(u.id, from1, to1, c.id, beds);
		//create booking that we time out
		Booking b2 = Booking.createBooking(u.id, from2, to2, c.id, beds);
		b2.status = Booking.CANCELLED;
		b2.update();
		
		List<Bed> beds1 = b1.beds;
		List<Bed> beds2 = b2.beds;

		for(Bed be: beds1) {
			//booking exists in range fromDate-toDate
			assertFalse(be.isAvailable(from1.minusDays(1), to1.plusDays(1)));
			assertFalse(be.isAvailable(from1, to1));
			assertFalse(be.isAvailable(from1.plusDays(1), to1.minusDays(1)));
			
			//booking doesn't exist in range fromDate-toDate 
			assertTrue(be.isAvailable(from1.minusWeeks(1), from1.minusDays(1)));
			assertTrue(be.isAvailable(to1, to1.plusDays(2)));
		}
		
		for(Bed be: beds2) {
			//booking exists but is timedout/cancelled
			assertTrue(be.isAvailable(from2, to2)); 
		}
	}
	
	@Test
	/**
	 * Tests that delete functions, deletes the bed
	 */
	public void testDelete() {
		LargeCabin ca = new LargeCabin("testDeleteBedCabin", 2);
		ca.save();
		List<Bed> beds = ca.beds;
		
		for(Bed b: beds){
			b.delete();
		}
		
		LargeCabin caI = (LargeCabin)Cabin.find.byId(ca.id);
		assertTrue(caI.beds.isEmpty());
	}
}
