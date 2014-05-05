package controllers;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

import org.junit.Before;
import org.junit.Test;

import models.Booking;
import models.LargeCabin;
import models.RDate;
import models.SmallCabin;
import models.User;
import play.test.WithApplication;

public class IntegrationTest extends WithApplication {

	SmallCabin sCabin;
	LargeCabin lCabin;
	User userOk;
	User userBad;
	final static String authToken = "X-AUTH-TOKEN";
	
	@SuppressWarnings("deprecation")
	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
		sCabin = new SmallCabin("AvailabilityTestSmallCabin");

		lCabin = new LargeCabin("AvailabilityTestLargeCabin", 8);
		userOk = new User("q@t","w", "t");
		userBad = new User("bad@guy.com", "w", "t");

		sCabin.save();
		lCabin.save();
		userOk.save();
	}
	
	@Test
	/** TE1.1
	 * Submit booking 
	 */
	public void testSubmitBooking() {
		
	}
	
	@Test
	/** TE1.2
	 * Cancel booking as customer
	 */
	public void testCancelBookingAsCustomer() {
		
	}
	
	@Test
	/** TE1.3
	 * Test that booking doesn't take place when over capacity of cabin
	 */
	public void testCapacityCheck() {
		
	}
	
	@Test
	/** TE1.4
	 * Reservation lock
	 */
	public void testReservationLock() {
		
	}
	
	@Test
	/** TE1.5
	 * Terms and conditions must be true before booking is accepted
	 */
	public void testTermsAndConditionsCheck() {
		
	}
	
	@Test
	/** TE1.6
	 * User must be logged in in order to submit booking
	 */
	public void testLoggedInBeforeSubmitBooking() {
		
	}
	
	/** ADMIN PART **/
	
	/* COMMENTING OUT, TE2.2 COVERS THIS TEST CASE
	@Test
	/** TE2.1
	 *  An admin should be able to create custom prices on each cabin.
	 *
	public void testAdminCanCreateCustomPrices() {
		
	}
	*/
	
	@Test
	/** TE2.2
	 *  An admin should be able to create custom customer prices 
	 *  (member vs non-member) with no limitations when it comes to amount of prices.
	 */
	public void testAdminCanCreateCustomPrices() {
		
	}
	
	@Test
	/** TE2.3
	 * Submit booking 
	 */
	public void SubmitBooking() {
		
	}
}
