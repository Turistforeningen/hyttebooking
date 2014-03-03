package models;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.*;

import org.junit.*;

import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class ModelsTest extends WithApplication{
	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
	}
	
	@Test
	public void createAndRetrieveBookings() {
		Date time =Calendar.getInstance().getTime();
		new Booking(time, "123456").save();
		Booking one = Booking.find.where().eq("userId", "123456").findUnique();
		assertNotNull(one);
		assertEquals(time, one.getDateOfBooking());
	}
	
	@Test
	public void checkUniqueId() {
		Date timeOfBooking =Calendar.getInstance().getTime();
		new Booking(timeOfBooking, "123456").save();
		Date timeOfBooking2 =Calendar.getInstance().getTime();
		new Booking(timeOfBooking2, "123456").save();
		List<Booking> res= Booking.find.where().eq("userId", "123456").findList();
		
		assertEquals(2, res.size());
		assertNotEquals(res.get(0).getId(), res.get(1).getId());
	}
	
	
}
