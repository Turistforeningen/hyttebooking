package controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.*;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.*;

import flexjson.JSONSerializer;
import static org.junit.Assert.*;
import play.mvc.Http.Status;
import play.mvc.Result;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class ControllerTest extends WithApplication {
	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
	}
	
	@Test
	public void checkCancel() {
		
	}
	
	
	@Test
	/** Don't know how to test controllers, so testing the functionality of the code **/
	public void TestGetAvailabilityForTimePeriodSmallCabin() {
		//make smallCabin with zero bookings
		SmallCabin sCabin = new SmallCabin("AvailabilityTest");
		sCabin.save();
		User user = new User("q@t","w", "t");
		user.save();
		
		/** add different bookings and test **/
		Date s1 = new DateTime("2014-03-01").toDate();
		Date e1 = new DateTime("2014-03-03").toDate();
		Booking b1 = Booking.createBooking(user.id, s1, e1, sCabin.id, null);
		b1.save();
		
		Date s2 = new DateTime("2014-04-25").toDate();
		Date e2 = new DateTime("2014-04-30").toDate();
		Booking b2 = Booking.createBooking(user.id, s2, e2, sCabin.id, null);
		b2.save();
		
		Date s3 = new DateTime("2014-04-15").toDate();
		Date e3 = new DateTime("2014-04-10").toDate();
		Booking b3 = Booking.createBooking(user.id, s3, e3, sCabin.id, null);
		b3.save();
		
		Date s4 = new DateTime("2014-03-29").toDate();
		Date e4 = new DateTime("2014-04-05").toDate();
		Booking b4 = Booking.createBooking(user.id, s4, e4, sCabin.id, null);
		b4.save();
		/** END adding bookings **/
		
		//parameters that should be gotten from JSON object
		DateTime startDate = new DateTime("2014-03-01");
		DateTime endDate = new DateTime("2014-04-30");
		long cabinId = sCabin.id;
	
		/**Start of test code, any debugging here should be transferred to BookingController method**/
		boolean[] bookedDays = new boolean[Math.abs(Days.daysBetween(startDate, endDate).getDays())+1];
		JSONSerializer serializer = new JSONSerializer();
		String actualResult;
		String expectedResult = "bookedDays [true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,true,true,true,true]";
		Boolean[] expectedResultBool = {true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,true,true,true,true};
		
		Cabin cabin = Cabin.find.byId(cabinId);
		if(cabin instanceof SmallCabin) {
			List<Booking> bookings = cabin.findAllBookingsForCabinGivenDate(cabinId, startDate, endDate);
			
			if(!bookings.isEmpty()) {
				for(Booking b: bookings) {
					//for each booking set bookedDays[i] = true for range startDate-endDate
					int[] indices = utilities.DateHelper.getIndex(startDate, new DateTime(b.dateFrom), new DateTime(b.dateTo)); /** indices[0] startIndex in bookedDays, [1] is endIndex **/
					if(indices[0] < 0) //if b.dateFrom precedes startDate, skip to startDate 
						indices[0] = 0;
					for(int i = indices[0]; i<=indices[1]; i++){
						bookedDays[i] = true; //TODO test
					}
				}
				actualResult = ("bookedDays "+ serializer.serialize(bookedDays));
			} else { //Either something is wrong or the entire given daterange shows available for given cabin
				actualResult = ("bookedDays " + serializer.serialize(bookedDays));
			}
		} else {
			actualResult = (" status " + "KO ");
			actualResult += ("message " + "date invalid");
			System.out.println("Bad request: "+actualResult);
		}
		if(cabin instanceof SmallCabin) {
			System.out.println("CompareStrings: "+actualResult.compareTo(expectedResult));
			assertTrue(0 == actualResult.compareTo(expectedResult));
		}
		else if(cabin instanceof LargeCabin)
			assertTrue(false/**TODO**/);
		
		/** END TEST **/
	}
	
	/**	BUGS: Expected results not similar to actual results, for some reason beds booked is -1 to expected result**/
	@Test
	public void TestGetAvailabilityForTimePeriodLargeCabin() {
		LargeCabin lCabin = new LargeCabin("AvailabilityTest", 8);
		lCabin.save();
		User user = new User("q@t","w", "t");
		user.save();
		
		//parameters that should be gotten from JSON object
		DateTime startDate = new DateTime("2015-03-01");
		DateTime endDate = new DateTime("2015-03-28");
		long cabinId = lCabin.id;
		
		int[] largeCBookedDays = new int[Math.abs(Days.daysBetween(startDate, endDate).getDays())+1];
		JSONSerializer serializer = new JSONSerializer();
		String actualResult;

		/** add different bookings and test **/
		Date s1 = new DateTime("2015-03-01").toDate();
		Date e1 = new DateTime("2015-03-03").toDate();
		List<Bed> arrBeds1 = ((LargeCabin) lCabin).book(1, new DateTime(s1), new DateTime(e1));
		Booking b1 = Booking.createBooking(user.id, s1, e1, lCabin.id, arrBeds1);
		b1.save();
		
		Date s2 = new DateTime("2015-03-09").toDate();
		Date e2 = new DateTime("2015-03-13").toDate();
		List<Bed> arrBeds2 = ((LargeCabin) lCabin).book(2, new DateTime(s2), new DateTime(e2));
		Booking b2 = Booking.createBooking(user.id, s2, e2, lCabin.id, arrBeds2);
		b2.save();
		
		Date s3 = new DateTime("2015-03-23").toDate();
		Date e3 = new DateTime("2015-03-28").toDate();
		List<Bed> arrBeds3 = ((LargeCabin) lCabin).book(3, new DateTime(s3), new DateTime(e3));
		Booking b3 = Booking.createBooking(user.id, s3, e3, lCabin.id, arrBeds3);
		b3.save();
		
		String expectedResult = "largeCBookedDays [1,1,1,0,0,0,0,0,2,2,2,2,2,0,0,0,0,0,0,0,0,0,3,3,3,3,3,3]";
		/** END adding bookings **/

		Cabin cabin = Cabin.find.byId(cabinId);
		if(cabin instanceof LargeCabin) {
			for(Bed beds : ((LargeCabin) cabin).beds) {
				for(Booking b : beds.bookings) {
					int[] indices = utilities.DateHelper.getIndex(startDate, new DateTime(b.dateFrom), new DateTime(b.dateTo));
					if(indices[0] < 0)
						indices[0] = 0;
					for(int i = indices[0]; i<=indices[1]; i++) {
						largeCBookedDays[i] += 1; //TODO SHOTGUN //blankets daterange with +1 to indicate that 1 bed is taken during that period
					}
				}
			}
			//TODO test
			actualResult = ("largeCBookedDays "+ serializer.serialize(largeCBookedDays));	
			System.out.println(actualResult);
		} else {
			actualResult = (" status " + "KO ");
			actualResult += ("message " + "date invalid");
			System.out.println("Bad request: "+actualResult);
		}
		
		assertTrue(0 == actualResult.compareTo(expectedResult));
	}
}
