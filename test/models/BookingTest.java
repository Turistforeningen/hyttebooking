package models;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import play.test.WithApplication;

public class BookingTest extends WithApplication {

	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
	}
	
	@Test
	public void testAddBed() {
		//bed
		
		//TODO
	}

	@Test
	public void testGetCabin() {

		//TODO
	}
	
	@Test
	public void testGetPayment() {

		//TODO
	}

	@Test
	public void testIsAbleToCancel() {

		//TODO
	}

	@Test
	public void testGetDeliveryDate() {

		//TODO
	}

	@Test
	public void testGetNrOfBeds() {

		//TODO
	}

	@Test
	public void testGetBookingPageByUser() {

		//TODO
	}

	@Test
	public void testGetBookingById() {

		//TODO
	}

	@Test
	public void testCreateBooking() {
		//userId, dateFrom, dateTo, cabinId, beds
		//TODO
	}
}
