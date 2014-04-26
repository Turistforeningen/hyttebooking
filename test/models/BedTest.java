package models;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

import java.util.List;

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

		DateTime from = RDate.fDt.plusDays(2);
		DateTime to = RDate.fDt.plusDays(10);

		//booking should save that beds are booked in the dateRange
		Booking b = Booking.createBooking(u.id, from, to, c.id, beds);
		List<Bed> newBeds = b.beds;

		for(Bed be: newBeds) {
			
			assertFalse(be.isAvailable(from.minusDays(1), to.plusDays(1)));
			assertFalse(be.isAvailable(from, to));
			assertFalse(be.isAvailable(from.plusDays(1), to.minusDays(1)));

			assertTrue(be.isAvailable(from.minusWeeks(1), from.minusDays(1)));
			assertTrue(be.isAvailable(to, to.plusDays(2)));
		}
	}
}
