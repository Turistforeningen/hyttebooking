package controllers;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import models.Booking;
import models.Cabin;
import models.Guest;
import models.LargeCabin;
import models.Payment;
import models.RDate;
import models.SmallCabin;
import models.User;
import play.mvc.Result;
import play.test.FakeRequest;
import play.test.WithApplication;
import play.libs.Json;
import utilities.BookingForm;

public class IntegrationTest extends WithApplication {

	LargeCabin lCabin;
	SmallCabin sCabin;
	User user;
	User admin;
	final static String authToken = "X-AUTH-TOKEN";
	
	@SuppressWarnings("deprecation")
	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
		//1st cabin, large
		lCabin = new LargeCabin("IntegrationTestLargeCabin", 8);
		//2nd cabin, small
		sCabin = new SmallCabin("IntegrationTestSmallCabin");
		user = new User("q@t","w", "t");
		admin = new User("admin", "p", "admin");
		admin.isAdmin = true;

		lCabin.save();
		sCabin.save();
		user.save();
		admin.save();
		
		Booking b1 = Booking.createBooking(user.id, RDate.fDt, RDate.fDt.plusDays(3), lCabin.id, lCabin.beds);
		b1.save();
		Payment p = Payment.createPaymentForBooking(user, b1, 1000.0, new ArrayList<Guest>());
		p.booking.status = Booking.PAID;
		b1.save();
	}
	
	@Test
	public void LargeCabin1Check() {
		assertTrue(Cabin.find.byId((long)1) instanceof LargeCabin);
	}
	
	/** TE1.1
	 * Submit booking 
	 * SEE testSubmitBooking in BookingControllerTest
	 */
	
	/** TE1.2
	 * Cancel booking as customer
	 * //SEE testCancelBooking in BookingControllerTest
	 */
	
	@Test
	/** TE1.3
	 * Test that booking doesn't take place when over capacity of cabin
	 */
	public void testCapacityCheck() {
		FakeRequest fkRequest = new FakeRequest(POST, "/api/bookings/");
		fkRequest.withHeader(authToken, user.createToken());
		fkRequest.withJsonBody(JsonHelper.getOverCapacityBooking(lCabin.beds.size()));
		
		Result resBad = route(fkRequest);
		assertEquals(BAD_REQUEST, status(resBad));
	}
	
	@Test
	/** TE1.4
	 * A system should reserve (still) unpaid reservation for 30 minutes
	 */
	public void testReservationLock() {
		//TODO as last test run
		assertTrue(true);
	}
	
	@Test
	/** TE1.5
	 * Terms and conditions must be true before booking is accepted
	 */
	public void testTermsAndConditionsCheck() {
		FakeRequest fkRequest = new FakeRequest(POST, "/api/bookings/");
		fkRequest.withHeader(authToken, user.createToken());
		fkRequest.withJsonBody(JsonHelper.getBadBooking());
		
		Result resBad = route(fkRequest);
		assertEquals(BAD_REQUEST, status(resBad));
	}
	
	@Test
	/** TE1.6
	 * User must be logged in in order to submit booking
	 */
	public void testLoggedInBeforeSubmitBooking() {
		FakeRequest fkRequest = new FakeRequest(POST, "/api/bookings/");
		//fkRequest.withHeader(authToken, user.createToken()); //not logged in
		fkRequest.withJsonBody(JsonHelper.getOkBooking());
		
		Result resBad = route(fkRequest);
		assertEquals(UNAUTHORIZED, status(resBad));
	}
	
	/** ADMIN PART **/

	/** TE2.1
	 *  An admin should be able to create custom prices on each cabin.
	 *  SEE TE2.2
	*/
	
	@Test
	/** TE2.2
	 *  An admin should be able to create custom customer prices 
	 *  (member vs non-member) with no limitations when it comes to amount of prices
	 */
	public void testAdminCanCreateCustomPrices() {
		//POST    /api/cabins/:cId/prices
		FakeRequest fkRequest = new FakeRequest(POST, "/api/cabins/1/prices");
		fkRequest.withHeader(authToken, admin.createToken());
		fkRequest.withJsonBody(JsonHelper.getPriceMinorFalse());
		
		Result resOk = route(fkRequest);
		assertEquals(OK, status(resOk));
		//TODO check that price can be retrieved from model that equals test price name
	}
	
	/** TE2.3
	 * An admin should be able to add a cabin 
	 * SEE testSubmitCabin in AdminControllerTest 
	 */
	
	@Test
	/**	TE2.4
	 * An admin should get a list of all the cabins
	 */
	public void testAdminCanGetCabinList() { //TODO
		//GET     /api/admin/cabins
		FakeRequest fkRequest = new FakeRequest(GET, "/api/admin/cabins");
		fkRequest.withHeader(authToken, admin.createToken());
		
		Result resOk = route(fkRequest);
		assertEquals(OK, status(resOk));
	}
	
	@Test
	/** TE2.5 & TE2.6
	 * An admin should be able to view all active bookings for a given cabin
	 * &
	 * The booking list in the back-office shall contain status labels telling if the booking is paid or not
	 */
	public void testAdminCanViewAllActiveBookingsForCabinAndViewStatus() {
		//GET     /api/admin/cabins/:id 
		FakeRequest fkRequest = new FakeRequest(GET, "/api/admin/cabins/1");
		fkRequest.withHeader(authToken, admin.createToken());
		
		Result resOk = route(fkRequest);
		assertEquals(OK, status(resOk));
		
		//TODO DESERIALIZE AND VIEW STATUS
		BookingForm bf = BookingForm.deserializeJson((contentAsString(resOk)));
	}
	
	@Test
	/** TE2.7
	 * An admin should be able to cancel a booking x days in advance. 
	 * The system should partially or fully refunds the booking.
	 */
	public void testAdminCanCancelAnyBooking() {
		//DELETE  /api/admin/bookings/:id
		FakeRequest fkRequest = new FakeRequest(DELETE, "/api/admin/bookings/1"); //call to cancel booking 1
		fkRequest.withHeader(authToken, admin.createToken());
		
		Result resOk = route(fkRequest);
		assertEquals(OK, status(resOk));
		JsonNode node = Json.parse(contentAsString(resOk));
		String msg = node.get("message").asText();
		assertTrue("Msg incorrect: "+msg+" - "+contentAsString(resOk), msg.equals("refund"));
	}
}
