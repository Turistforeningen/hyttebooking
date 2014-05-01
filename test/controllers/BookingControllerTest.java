package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.Bed;
import models.Booking;
import models.Cabin;
import models.LargeCabin;
import models.SmallCabin;
import models.User;
import modelsTests.*;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import flexjson.JSONSerializer;
import static org.junit.Assert.*;
import play.libs.Json;
import play.mvc.Http.Status;
import play.mvc.Result;
import play.test.FakeRequest;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class BookingControllerTest extends WithApplication {

	SmallCabin sCabin;
	LargeCabin lCabin;
	User user;

	String jsonString = "{\"cabinId\":\"1\",\"dateTo\":\"2014-05-22\",\"dateFrom\":\"2014-05-15\","
			+ "\"guests\":[{\"id\":1,\"ageRange\":\"26 og opp\",\"guestType\":\"Voksen, medlem\","
			+ "\"nr\":3,\"price\":300,\"isMember\":true},{\"id\":2,\"ageRange\":\"13-25\","
			+ "\"guestType\":\"Ungdom, medlem\",\"nr\":0,\"price\":200,\"isMember\":true},"
			+ "{\"id\":3,\"ageRange\":\"4-12\",\"guestType\":\"Barn, medlem\",\"nr\":0,\"price\":100,"
			+ "\"isMember\":true},{\"id\":4,\"ageRange\":\"0-4\",\"guestType\":\"Spedbarn, medlem\","
			+ "\"nr\":0,\"price\":0,\"isMember\":true},{\"id\":1,\"ageRange\":\"26 og opp\","
			+ "\"guestType\":\"Voksen,\",\"nr\":0,\"price\":400,\"isMember\":false},{\"id\":2,"
			+ "\"ageRange\":\"13-25\",\"guestType\":\"Ungdom,\",\"nr\":0,\"price\":300,\"isMember\":false},"
			+ "{\"id\":3,\"ageRange\":\"4-12\",\"guestType\":\"Barn,\",\"nr\":0,\"price\":200,"
			+ "\"isMember\":false},{\"id\":4,\"ageRange\":\"0-4\",\"guestType\":\"Spedbarn,\","
			+ "\"nr\":0,\"price\":0,\"isMember\":false}],\"termsAndConditions\":true}"; //working with static data for now
	//surely a more dynamic and easy on the eyes way TODO find out

	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
		sCabin = new SmallCabin("AvailabilityTestSmallCabin");

		lCabin = new LargeCabin("AvailabilityTestLargeCabin", 8);
		user = new User("q@t","w", "t");

		sCabin.save();
		lCabin.save();
		user.save();
	}

	@Test
	/**
	 * Test that empty request returns null result
	 * Test that booking with only minors/babies returns badRequest
	 * Test that termsAndConditions false returns badRequest
	 * http://www.playframework.com/documentation/2.1.x/api/java/play/test/FakeRequest.html
	 */
	public void testSubmitBooking() {
		//TODO make one of these for each test
		//FakeRequest can be configured to have any additional plugins, 
		//configurations or globals (maybe even @WithSecurityControllor
		FakeRequest fkRequest = new FakeRequest("/POST", "api/bookings/");
		JsonNode data = Json.parse(jsonString);
		fkRequest.withJsonBody(data);

		//fkRequest
		//TODO make one of these for each fakeRequest
		//Result result = callAction(controllers.routes.BookingController.submitBooking(), fkRequest);
		//TODO find out how controllers...submitBooking() returns a handler reference

		//TODO make one of these for different results
		//assertEquals(badRequest(), result);
	}

	@Test
	/**
	 * Test that empty request returns badRequest
	 * Test that attempt to cancel non-existant booking returns badRequest
	 * 
	 */
	public void testCancelBooking()  {
		//TODO make one of these for each test
		FakeRequest fkRequest = new FakeRequest("/DELETE", "api/bookings/");
		//TODO how do we add query parameter to the fake request?
	
		//don't need to use deprecated routeAndCall, 
		//the appropriate call is automatically chosen with new route method 
		Result res1 = route(fkRequest);
		System.out.println(res1);
		//TODO make one of these for each fakeRequest
		//Result result = callAction(controllers.routes.BookingController.cancelBooking(), fkRequest);
		//TODO find out how controllers...cancelBooking() returns a handler reference

		//TODO make one of these for different results
		//assertEquals(badRequest(), result);
	}
	
	@Test
	/**
	 * 
	 */
	public void getOrderSummary() {
		
	}

	@Test
	/** Note: Doesn't test the actual controller, just copied the entire code and testing within here **/
	public void TestGetAvailabilityForTimePeriodSmallCabin() {
		//make smallCabin with zero bookings

		/** add different bookings and test **/
		DateTime s1 = new DateTime("2015-03-01");
		DateTime e1 = new DateTime("2015-03-03");

		Booking b1 = Booking.createBooking(user.id, s1, e1, sCabin.id, null);
		b1.save();
		b1.status = Booking.CANCELLED;
		b1.update();

		DateTime s2 = new DateTime("2015-04-25");
		DateTime e2 = new DateTime("2015-04-30");
		Booking b2 = Booking.createBooking(user.id, s2, e2, sCabin.id, null);
		b2.save();

		//DateTime s3 = new DateTime("2015-04-15");
		//DateTime e3 = new DateTime("2015-04-10");
		//Booking b3 = Booking.createBooking(user.id, s3, e3, sCabin.id, null); Booking can't happen test is else where now
		//b3.save();

		DateTime s4 = new DateTime("2015-03-29");
		DateTime e4 = new DateTime("2015-04-05");
		Booking b4 = Booking.createBooking(user.id, s4, e4, sCabin.id, null);
		b4.save();

		int[] expectedResultArray = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1};
		String expectedResultString = "bookedDays "+expectedResultArray;
		/** END adding bookings **/

		//parameters that should be gotten from calendar module (query parameters)
		DateTime startDate = new DateTime("2015-03-01");
		DateTime endDate = new DateTime("2015-04-30");
		long cabinId = sCabin.id;

		/**Start of test code, any debugging here should be transferred to BookingController method**/
		int[] smallCabinBookedDays = new int[Math.abs(Days.daysBetween(startDate, endDate).getDays())+1];
		JSONSerializer serializer = new JSONSerializer();
		String actualResult;

		Cabin cabin = Cabin.find.byId(cabinId);
		if(cabin instanceof SmallCabin) {
			List<Booking> bookings = SmallCabin.findAllBookingsForCabinGivenDate(cabinId, startDate, endDate);

			if(!bookings.isEmpty()) {
				for(Booking b: bookings) {
					//for each booking set bookedDays[i] = +1 for range startDate-endDate
					int[] indices = utilities.DateHelper.getIndex(startDate, new DateTime(b.dateFrom), new DateTime(b.dateTo)); /** indices[0] startIndex in bookedDays, [1] is endIndex **/
					if(indices[0] < 0) //if b.dateFrom precedes startDate, skip to startDate 
						indices[0] = 0;
					for(int i = indices[0]; i<=indices[1]; i++){
						smallCabinBookedDays[i] = 1; //TODO test
					}
				}
				actualResult = ("bookedDays "+ serializer.serialize(smallCabinBookedDays));
			} else { //Either something is wrong or the entire given daterange shows available for given cabin
				actualResult = ("bookedDays " + serializer.serialize(smallCabinBookedDays));
			}
		} else {
			actualResult = (" status " + "KO ");
			actualResult += ("message " + "date invalid");
			System.out.println("Bad request: "+actualResult);
		}

		//System.out.println("ACTUAL: "+Arrays.toString(smallCabinBookedDays));
		//System.out.println("EXPECTED: "+Arrays.toString(expectedResultArray));
		assertArrayEquals(expectedResultArray, smallCabinBookedDays);
	}

	/** Note: Doesn't actually test the controllers, just copied the entire code and testing within here **/
	@Test
	public void TestGetAvailabilityForTimePeriodLargeCabin() {

		//parameters that should be gotten from JSON object
		DateTime startDate = new DateTime("2015-03-01");
		DateTime endDate = new DateTime("2015-03-28");
		long cabinId = lCabin.id;

		int[] largeCabinBookedDays = new int[Math.abs(Days.daysBetween(startDate, endDate).getDays())+1];
		JSONSerializer serializer = new JSONSerializer();
		String actualResult;

		/** add different bookings and test **/
		DateTime s1 = new DateTime("2015-03-01");
		DateTime e1 = new DateTime("2015-03-03");
		List<Bed> arrBeds1 = ((LargeCabin) lCabin).book(1, new DateTime(s1), new DateTime(e1));
		Booking b1 = Booking.createBooking(user.id, s1, e1, lCabin.id, arrBeds1);
		b1.save();

		DateTime s2 = new DateTime("2015-03-09");
		DateTime e2 = new DateTime("2015-03-13");
		List<Bed> arrBeds2 = ((LargeCabin) lCabin).book(2, new DateTime(s2), new DateTime(e2));
		Booking b2 = Booking.createBooking(user.id, s2, e2, lCabin.id, arrBeds2);
		b2.save();

		DateTime s3 = new DateTime("2015-03-23");
		DateTime e3 = new DateTime("2015-03-28");
		List<Bed> arrBeds3 = ((LargeCabin) lCabin).book(3, new DateTime(s3), new DateTime(e3));
		Booking b3 = Booking.createBooking(user.id, s3, e3, lCabin.id, arrBeds3);
		b3.save();

		int[] expectedResultArray = {1,1,1,0,0,0,0,0,2,2,2,2,2,0,0,0,0,0,0,0,0,0,3,3,3,3,3,3};
		/** END adding bookings **/

		Cabin cabin = Cabin.find.byId(cabinId);
		if(cabin instanceof LargeCabin) {
			for(Bed beds : ((LargeCabin) cabin).beds) {
				for(Booking b : beds.bookings) {
					int[] indices = utilities.DateHelper.getIndex(startDate, new DateTime(b.dateFrom), new DateTime(b.dateTo));
					if(indices[0] < 0) //if b.dateFrom precedes startDate, skip to startDate 
						indices[0] = 0;
					for(int i = indices[0]; i<=indices[1]; i++) {
						largeCabinBookedDays[i] += 1; //blankets daterange with +1 to indicate that 1 extra bed is taken during that period
					}
				}
			}
		} else {
			actualResult = (" status " + "KO ");
			actualResult += ("message " + "date invalid");
			System.out.println("Bad request: "+actualResult);
		}

		//System.out.println("ACTUAL: "+Arrays.toString(largeCabinBookedDays));
		//System.out.println("EXPECTED: "+Arrays.toString(expectedResultArray));
		assertArrayEquals(expectedResultArray, largeCabinBookedDays);
	}
}
