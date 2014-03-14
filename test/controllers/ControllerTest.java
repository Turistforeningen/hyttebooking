package controllers;

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
		String expectedResult = "bookedDays [true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false]";
		
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
				System.out.println(actualResult);
			} else { //Either something is wrong or the entire given daterange shows available for given cabin
				actualResult = ("bookedDays " + serializer.serialize(bookedDays));
				System.out.println(actualResult);	
			}
		} else {
			actualResult = (" status " + "KO ");
			actualResult += ("message " + "date invalid");
			System.out.println("Bad request: "+actualResult);
		}
		assertTrue(actualResult.equals(expectedResult));
		
		/** END TEST **/
	}
}
