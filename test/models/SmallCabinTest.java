package models;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import models.Booking;
import models.SmallCabin;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.WithApplication;

public class SmallCabinTest extends WithApplication {

	/** Create classes to be used in the tests **/
	private SmallCabin s1;
	private SmallCabin s2;
	private User u;
	
	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
		s1 = new SmallCabin("SmallCabinTestSmallCabin1");
		s2 = new SmallCabin("SmallCabinTestSmallCabin2");
		u = new User("u@SmallCabinTest.no", "password1", "SmallCabinTestUser");
		
		s1.save();
		s2.save();
		u.save();
		
		Booking b1; //this booking not created
		Booking b2 = Booking.createBooking(u.id, RDate.fDt.plusWeeks(4), RDate.fDt.plusWeeks(8), s1.id, null);
		Booking b3 = Booking.createBooking(u.id, RDate.fDt.plusWeeks(9), RDate.fDt.plusWeeks(13), s1.id, null);
	}
	
	@Test
	/**
	 * Test that cabin with no bookings returns no bookings
	 * Test that cabin with bookings returns all these bookings
	 */
	public void testFindAllBookingsForCabinGivenDate() {
		//Test that cabin with bookings returns all these bookings
		assertEquals(1, SmallCabin.findAllBookingsForCabinGivenDate(s1.id, RDate.fDt, RDate.fDt.plusWeeks(6)).size());
		assertEquals(2, SmallCabin.findAllBookingsForCabinGivenDate(s1.id, RDate.fDt, RDate.fDt.plusWeeks(10)).size());
		assertEquals(1, SmallCabin.findAllBookingsForCabinGivenDate(s1.id, RDate.fDt.plusWeeks(10), RDate.fDt.plusWeeks(14)).size());
		//Test that cabin with no bookings returns no bookings
		assertEquals(0, SmallCabin.findAllBookingsForCabinGivenDate(s2.id, RDate.fDt, RDate.fDt.plusMonths(5)).size());
		
		//Test that cabin with bookings returns all these bookings (even when fromDate-toDate is contained within booking's fromDate-toDate)
		assertEquals(1, SmallCabin.findAllBookingsForCabinGivenDate(s1.id, RDate.fDt.plusWeeks(6), RDate.fDt.plusWeeks(7)).size());
		
		//Test that cabin with bookings returns no bookings when fromDate-toDate range is outside of these bookings fromDate-toDate range
		assertEquals(0, SmallCabin.findAllBookingsForCabinGivenDate(s1.id, RDate.fDt, RDate.fDt.plusDays(10)).size());
	}
}
