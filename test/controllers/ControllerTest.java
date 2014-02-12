package controllers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.*;

import org.junit.*;

import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class ControllerTest extends WithApplication {
	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
	}
	
	@Test
	public void checkCancel() {
		Date timeOfBooking =Calendar.getInstance().getTime();
		Date start = Calendar.getInstance().getTime();
		start.setYear(start.getYear()+1);
		Date end = Calendar.getInstance().getTime();
		end.setYear(start.getYear()+2);
		Booking b = new Booking(timeOfBooking, "123456", start, end, "Heifjord");
		b.save();
		controllers.Application.cancelBooking(b.getId());
		Booking b2 = Booking.find.where().eq("id", b.getId()).findUnique();
		assertNull(b2);
	}
	
}
