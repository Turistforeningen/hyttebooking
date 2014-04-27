package models;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import play.test.WithApplication;

public class BookingTest extends WithApplication {

	LargeCabin c;
	User u;
	int NROFBEDS = 10;

	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
		c = new LargeCabin("BookingTestCabin", NROFBEDS);
		u = new User("u@u.no", "password1", "BedTestModelUser1");
		c.save();
		u.save();
	}

	@Test
	/**
	 * Test that addBed adds beds
	 */
	public void testAddBed() {
		List<Bed> beds = c.beds;

		DateTime from = RDate.fDt.plusDays(2);
		DateTime to = RDate.fDt.plusDays(10);

		Booking b = Booking.createBooking(u.id, from, to, c.id, beds);
		assertEquals(NROFBEDS, b.beds.size());

		List<Bed> newBeds = new ArrayList<Bed>();
		for(int i = 0; i<NROFBEDS; i++){
			newBeds.add(new Bed());
		}
		
		for(Bed be: newBeds)
			b.addBed(be);
		b.update();
		
		//Test that addBed adds beds, by adding the beds twice (in constructor and here)
		//System.out.println("NROFBEDS "+NROFBEDS + " - "+b.beds.size());
		assertEquals(NROFBEDS*2, b.beds.size());
	}

	@Test
	/**
	 * Test isAbleToCancel won't return false when it shouldn't:
	 * - Based on time of day (should only rollover on next date)
	 * Test isAbleToCancel won't return true when it should:
	 */
	public void testIsAbleToCancel() {
		DateTime from1 = DateTime.now().plusDays(1);
		DateTime to1 = DateTime.now().plusDays(3);

		DateTime from2 = DateTime.now().plusDays(6);
		DateTime to2 = DateTime.now().plusWeeks(2);

		DateTime from3 = DateTime.now().plusDays(7);
		DateTime to3 = DateTime.now().plusWeeks(2);

		DateTime from4 = DateTime.now().plusDays(7).withTimeAtStartOfDay();
		DateTime to4 = DateTime.now().plusWeeks(2);

		Booking b1 = Booking.createBooking(u.id, from1, to1, c.id, c.beds);
		Booking b2 = Booking.createBooking(u.id, from2, to2, c.id, c.beds);
		Booking b3 = Booking.createBooking(u.id, from3, to3, c.id, c.beds);
		Booking b4 = Booking.createBooking(u.id, from4, to4, c.id, c.beds);

		assertFalse(b1.isAbleToCancel());
		assertFalse(b2.isAbleToCancel());
		assertTrue(b3.isAbleToCancel());
		assertTrue(b4.isAbleToCancel());
	}

	@Test
	/**
	 * Test that createBooking doesn't create booking if beds is null and cabin type is largeCabin
	 * Test that createBooking doesn't create booking if user with userId not found
	 * Test that createBooking doesn't create booking if dateFrom predates dateTo
	 * Test that createBooking doesn't create booking if dateFrom is before today
	 * Test that after a booking is created, finder retrieves object
	 */
	public void testCreateBooking() {
		Booking b1 = Booking.createBooking(u.id, RDate.fDt, RDate.fDt.plusDays(4), c.id, null);
		Booking b2 = Booking.createBooking(new Long(1131), RDate.fDt.plusWeeks(1), RDate.fDt.plusWeeks(2), c.id, c.beds);
		Booking b3 = Booking.createBooking(u.id, RDate.fDt.plusMonths(1), RDate.fDt.plusWeeks(3), c.id, c.beds);
		Booking b4 = Booking.createBooking(u.id, DateTime.now().minusDays(1), DateTime.now().plusDays(1), c.id, c.beds);
		Booking b5 = Booking.createBooking(u.id, RDate.fDt.plusMonths(1), RDate.fDt.plusWeeks(8), c.id, c.beds);
		
		assertNull(b1); //Test that createBooking doesn't create booking if beds is null and cabin type is largeCabin
		assertNull(b2); //Test that createBooking doesn't create booking if user with userId not found
		assertNull(b3); //Test that createBooking doesn't create booking if dateFrom predates dateTo
		assertNull(b4); //Test that createBooking doesn't create booking if dateFrom before today
		assertEquals(b5.id, Booking.find.byId(b5.id).id); //Test that after a booking is created, finder retrieves object
	}
}
