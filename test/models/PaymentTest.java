package models;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

import org.junit.Before;
import org.junit.Test;

import play.test.WithApplication;

public class PaymentTest extends WithApplication {

	private LargeCabin c1;
	private SmallCabin s1;
	private User u1;
	private User u2;
	private Booking b1;
	
	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
		c1 = new LargeCabin("PaymentTestLargeCabin1", 10);
		s1 = new SmallCabin("PaymentTestSmallCabin1");
		u1 = new User("u@PaymentTest.no", "password1", "PaymentTestUser");
		
		c1.save();
		s1.save();
		u1.save();
		
		//to user 1
		b1 = Booking.createBooking(u1.id, RDate.fDt.plusMonths(1), RDate.fDt.plusMonths(2), c1.id, c1.beds);
	}
	
	@Test
	/**
	 * Test that payment can't be created with negative amount
	 * Test that payment must be linked to booking (not null)
	 * Test that payment must be linked to user (not null)
	 * Test that booking is linked to user
	 * Test that booking has payment linked to it and vice versa
	 */
	public void testCreatePaymentForBooking() {
		double valid 	= 1000.0;
		double invalid 	= -1000.0;
		
		//Test that payment can't be created with negative amount
		assertNull(Payment.createPaymentForBooking(u1, b1, invalid));
		
		//Test that payment must be linked to booking (not null)
		assertNull(Payment.createPaymentForBooking(u1, null, valid));
		
		//Test that payment must be linked to user (not null)
		assertNull(Payment.createPaymentForBooking(null, b1, valid));
		
		//Test that booking is linked to user
		assertNull(Payment.createPaymentForBooking(u2, b1, valid));
		
		//Test that booking has payment linked to it and vice versa
		Payment p = Payment.createPaymentForBooking(u1, b1, valid);
		assertNotNull(p);
		assertEquals(p.booking.id, b1.id);
		assertEquals(b1.payment.id, p.id);
	}
}
