package models;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

import org.junit.Before;
import org.junit.Test;

import play.test.WithApplication;

public class PaymentTest extends WithApplication {

	private LargeCabin c1;
	private SmallCabin s1;
	private User u;
	
	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
		c1 = new LargeCabin("PaymentTestLargeCabin1", 10);
		s1 = new SmallCabin("PaymentTestSmallCabin1");
		u = new User("u@PaymentTest.no", "password1", "PaymentTestUser");
		
		c1.save();
		s1.save();
		u.save();
		
		Booking b1 = Booking.createBooking(u.id, RDate.fDt.plusMonths(1), RDate.fDt.plusMonths(2), c1.id, c1.beds);
		Booking b2 = Booking.createBooking(u.id, RDate.fDt.plusMonths(3), RDate.fDt.plusMonths(4), c1.id, c1.beds);
		
	}
	
	@Test
	/**
	 * Test that payment can't be created with negative amount
	 * Test that payment must be linked to booking (not null)
	 * Test that payment must be linked to user (not null)
	 */
	public void testCreatePaymentForBooking() {
		
	}
}
