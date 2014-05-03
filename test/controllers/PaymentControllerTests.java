package controllers;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

import java.util.ArrayList;
import java.util.List;

import models.Booking;
import models.Guest;
import models.LargeCabin;
import models.Payment;
import models.RDate;
import models.SmallCabin;
import models.User;

import org.junit.Before;

import play.test.WithApplication;

public class PaymentControllerTests extends WithApplication {

	SmallCabin sCabin;
	LargeCabin lCabin;
	User userOk;
	User userBad;
	final static String authToken = "X-AUTH-TOKEN";
	Payment p;
	
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
		List<Guest> guests = new ArrayList<Guest>();
		
		Booking b1 = Booking.createBooking(userOk.id, RDate.fDt, RDate.fDt.plusDays(3), lCabin.id, lCabin.beds);
		b1.save();
		p = Payment.createPaymentForBooking(userOk, b1, 1000, guests);
	}
	
	//@Test
	//Is there even a point in testing this, should be waterproof
}
