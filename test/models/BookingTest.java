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

		System.out.println("From4: "+from4 + ": To4"+to4);
		System.out.println("Is "+DateTime.now().plusDays(7)+" after "+from4+"?"); //7 here is cancellation limit, but arbitrary so doesn't really matter
		System.out.println(DateTime.now().plusDays(7).isAfter(from4.getMillis()));

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
	public void testGetDeliveryDate() {

		//TODO
	}

	@Test
	public void testGetNrOfBeds() {

		//TODO
	}

	@Test
	public void testGetBookingPageByUser() {

		//TODO
	}

	@Test
	public void testGetBookingById() {

		//TODO
	}

	@Test
	public void testCreateBooking() {
		//userId, dateFrom, dateTo, cabinId, beds
		//TODO
	}
}
