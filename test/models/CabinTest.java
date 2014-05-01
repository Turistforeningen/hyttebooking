package models;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import models.Booking;
import models.Cabin;
import models.LargeCabin;
import models.SmallCabin;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.WithApplication;

public class CabinTest extends WithApplication {

	/** Create classes to be used in the tests **/
	private LargeCabin c1;
	private LargeCabin c2;
	private SmallCabin s1;
	private SmallCabin s2;
	private User u;
	
	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
		c1 = new LargeCabin("CabinTestLargeCabin1", 10);
		c2 = new LargeCabin("CabinTestLargeCabin2", 10);
		s1 = new SmallCabin("CabinTestSmallCabin1");
		s2 = new SmallCabin("CabinTestSmallCabin2");
		u = new User("u@CabinTest.no", "password1", "CabinTestUser");
		
		c1.save();
		c2.save();
		s1.save();
		s2.save();
		u.save();
	}
	
	@Test
	/**
	 * Test that cabin with no bookings returns no bookings
	 * Test that cabin with bookings returns all these bookings
	 * Test that negative page size is refused
	 * Test that negative pageSize is refused
	 */
	public void testFindAllBookingsForCabin() {
		Booking b1; //this booking not created
		Booking b2 = Booking.createBooking(u.id, RDate.fDt.plusMonths(1), RDate.fDt.plusMonths(2), c2.id, c2.beds);
		Booking b3 = Booking.createBooking(u.id, RDate.fDt.plusMonths(3), RDate.fDt.plusMonths(4), c2.id, c2.beds);
		Booking b4 = Booking.createBooking(u.id, RDate.fDt.plusMonths(1), RDate.fDt.plusMonths(2), s1.id, null);
		Booking b5 = Booking.createBooking(u.id, RDate.fDt.plusMonths(1), RDate.fDt.plusMonths(2), s2.id, null);
		int posPageSize = 5;
		int negPageSize = -5;
		int posPage = 5;
		int negPage = -5;
		
		//Test that cabin with no bookings returns no bookings
		assertEquals(0, (int)Cabin.findAllBookingsForCabin(c1.id, posPage, posPageSize).totalItems);
		//Test that cabin with bookings returns all these bookings (also tests some parts og pagination)
		assertEquals(2, (int)Cabin.findAllBookingsForCabin(c2.id, posPage, posPageSize).totalItems);
		assertEquals(1, (int)Cabin.findAllBookingsForCabin(s1.id, posPage, posPageSize).totalItems);
		assertEquals(1, (int)Cabin.findAllBookingsForCabin(s2.id, posPage, posPageSize).totalItems);
		
		//Test that negative page size is refused
		assertNull(Cabin.findAllBookingsForCabin(c2.id, negPage, posPageSize));
		//Test that negative pageSize is refused
		assertNull(Cabin.findAllBookingsForCabin(c2.id, posPage, negPageSize));
	}
	
	@Test
	/**
	 * Test that all cabins returned when they exist
	 * Test that no cabins returned when page negative
	 * Test that no cabins returned when pageSize negative
	 * Test that no cabins returned when they don't exist
	 */
	public void testFindAllCabins() {
		int posPageSize = 5;
		int negPageSize = -5;
		int posPage = 5;
		int negPage = -5;
		
		//Test that all cabins returned when they exist
		assertEquals(4, (int)Cabin.findAllCabins(posPage, posPageSize).totalItems);
		
		//Test that no cabins returned when page negative
		assertNull(Cabin.findAllCabins(negPage, posPageSize));
		//Test that no cabins returned when pageSize negative
		assertNull(Cabin.findAllCabins(posPage, negPageSize));
		
		//delete
		c1.delete();
		c2.delete();
		s1.delete();
		s2.delete();
		//Test that no cabins returned when they don't exist
		assertEquals(0, (int)Cabin.findAllCabins(posPage, posPageSize).totalItems);
	}
}
