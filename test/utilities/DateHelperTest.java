package utilities;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import play.test.WithApplication;
import models.RDate;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

public class DateHelperTest extends WithApplication  {

	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
	}
	
	@Test
	public void testWithinDate() {
		DateTime a = new DateTime().now().withTimeAtStartOfDay();
		DateTime b = new DateTime().now().withTimeAtStartOfDay().plusDays(6).minus(1);
		
		DateTime p1 = new DateTime().now().plusDays(2); //within = true
		DateTime p2 = new DateTime().now().plusDays(5); //within = true
		DateTime p3 = new DateTime().now(); //within = true
		DateTime p4 = new DateTime().now().plusDays(6); //within = false
		DateTime p5 = new DateTime().now().minusDays(1); //within = false
		
		assertTrue(utilities.DateHelper.withinDate(p1, a, b));
		assertTrue(utilities.DateHelper.withinDate(p2, a, b));
		assertTrue(utilities.DateHelper.withinDate(p3, a, b));
		assertFalse(utilities.DateHelper.withinDate(p4, a, b));
		assertFalse(utilities.DateHelper.withinDate(p5, a, b));
	}
	
	@Test
	public void testGetIndex() {
		DateTime start = new DateTime().now(); //this is 0
		
		DateTime a = new DateTime().now().plusDays(1); // +1
		DateTime b = new DateTime().now().minusDays(1); // -1
		DateTime c = new DateTime().now(); // 0

		assertEquals(1, utilities.DateHelper.getIndex(start, a, b)[0]); //days between start and a
		assertEquals(-1, utilities.DateHelper.getIndex(start, a, b)[1]); //days between start and b
		assertEquals(0, utilities.DateHelper.getIndex(start, a, c)[1]); //days between start and c
	}
	
	@Test
	public void testValid() {
		DateTime pivot = RDate.fDt;
		DateTime after = RDate.fDt.plusDays(10);
		DateTime before = RDate.fDt.minusDays(10);
		
		assertTrue(utilities.DateHelper.valid(pivot, after));
		assertFalse(utilities.DateHelper.valid(pivot, before));
		assertTrue(utilities.DateHelper.valid(DateTime.now().withTimeAtStartOfDay().plus(1), after));
	}
	
	@Test
	public void testIsOverLap() {
		//TODO 
		//La oss det finnes en booking fra fredag til søndag
		//Også søker vi om det finnes bookings i tidsrommet mandag-fredag 
		//burde den bookingen fra fredag til søndag returnere true?
		
		/*
		 * ##### BOOKING: 20150305 #-# 20150402
		 * ##### SEARCH: 20150101 #-# 20150305
		 * ##### OVERLAP? false
		 * Do we want this to be true?
		 */
	}
	
	@Test
	public void testIsValidTimeStamp() {
		assertTrue(utilities.DateHelper.isValidTimeStamp(DateTime.now().getMillis() / 1000));
		assertFalse(utilities.DateHelper.isValidTimeStamp(DateTime.now().getMillis() / 1000 + 65));
		assertFalse(utilities.DateHelper.isValidTimeStamp(DateTime.now().getMillis() / 1000 - 65));
	}
}
