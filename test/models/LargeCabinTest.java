package models;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static org.junit.Assert.*;
import models.Bed;
import models.LargeCabin;
import models.User;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import play.test.WithApplication;

public class LargeCabinTest extends WithApplication {

	/** Create classes to be used in the tests **/
	private LargeCabin c1;
	private LargeCabin c2;
	private User u;
	private final static int NROFBEDS = 10;
	
	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
		
		c1 = new LargeCabin("LargeCabinTestCabin1", NROFBEDS);
		c2 = new LargeCabin("LargeCabinTestCabin2", NROFBEDS);
		u = new User("u@LargeCabinTest.no", "password1", "LargeCabinTestUser");
		
		c1.save();
		c2.save();
		u.save();
	}
	
	@Test
	/**
	 * Test that bed created is linked to cabin that called addBed
	 * Test that cabins number of beds grows expectedly
	 */
	public void testAddBed() {
		int prevNrOfBeds = c1.beds.size();
		c1.addBed();
		c1.update();
		
		//Test that cabins number of beds grows expectedly
		assertEquals(prevNrOfBeds+1, c1.beds.size());
		
		//Test that bed created is linked to cabin that called addBed
		Bed bed = c1.beds.get(c1.beds.size()-1); //get last bed added
		assertEquals(bed.largeCabin.id, c1.id);
	}

	@Test
	/**
	 * Test that if negative numberOfBeds null is returned 
	 * Test that if invalid fromDate-toDate (before today, toDate before fromDate) null is returned
	 * Test that if availBeds.size() < numberOfBeds null is returned
	 * Test that if valid numberOfBeds, returned availBeds.size() == numberOfBeds (i.e. request matches response)
	 */
	public void testBook() {
		int posNrOfBeds = 3;
		int negNrOfBeds = -3;
		int tooLargeNrOfBeds = NROFBEDS+1;
		DateTime validDateFrom = RDate.fDt;
		DateTime validDateTo = RDate.fDt.plusDays(10);
		DateTime invalidDateFrom = DateTime.now().withTimeAtStartOfDay().minus(1); //exactly invalid (1 ms before today)
		DateTime invalidDateTo = validDateFrom.minus(1); //exactly invalid (1 ms before validDateFrom)
		
		//Test that if negNrOfBeds null is returned
		assertNull(c1.book(negNrOfBeds, validDateFrom, validDateTo)); 
		
		//Test that if invalid fromDate-toDate null is returned (before today)
		assertNull(c1.book(posNrOfBeds, invalidDateFrom, validDateTo));
		//Test that if invalid fromDate-toDate null is returned (before dateFrom)
		assertNull(c1.book(posNrOfBeds, validDateFrom, invalidDateTo));
		
		//Test that if NROFBEDS < posNrOfBeds null is returned		
		assertNull(c1.book(tooLargeNrOfBeds, validDateFrom, validDateTo));
		
		//Test that if valid numberOfBeds, returned availBeds.size() == numberOfBeds (i.e. request matches response)
		assertEquals(posNrOfBeds, c1.book(posNrOfBeds, validDateFrom, validDateTo).size());
	}
	
	@Test
	/**
	 * Test that bed removed not linked to cabin anymore (should not exist)
	 * Test that cabins beds.size() decreases expectedly
	 * Test that remove bed can remove more than one bed if more than two beds exist
	 */
	public void testRemoveBed() {
		int prevNrOfBeds = c1.beds.size();
		Bed bed1 = c1.beds.get(0); //get first bed (this is the one always removed)
		c1.removeBed();
		c1.update();
		Bed bed2 = c1.beds.get(0); 
		
		//Test that remove bed can remove more than one bed if more than two beds exist
		assertNotNull(bed2); 
		//Test that cabins beds.size() decreases expectedly
		assertEquals(prevNrOfBeds-1, c1.beds.size());
		//Test that bed removed not linked to cabin anymore (should not exist)
		assertNull(bed1.largeCabin); 
	}
	
	@Test
	/**
	 * Test that adding price will create priceMatrix if one doesn't exist
	 * Test that added price can be retrieved
	 * Test that prices cannot be negative
	 */
	public void testAddPrice() {
		int validPrice = 1000;
		int invalidPrice = -1000;
		int expectedPriceMatrixSize;
		
		c1.addPrice("guestType1", "minAge-maxAge", validPrice, validPrice);
		assertNotNull(c1.priceMatrix);
		expectedPriceMatrixSize = c1.priceMatrix.size();
		
		c1.addPrice("guestType2", "minAge-maxAge", invalidPrice, validPrice);
		assertEquals(expectedPriceMatrixSize, c1.priceMatrix.size()); //no change since previous price invalid
		c1.addPrice("guestType2", "minAge-maxAge", validPrice, invalidPrice);
		assertEquals(expectedPriceMatrixSize, c1.priceMatrix.size()); //no change since previous price invalid

		c1.addPrice("", "minAge-maxAge", validPrice, validPrice);
		assertEquals(expectedPriceMatrixSize, c1.priceMatrix.size()); //no change since previous price invalid
		
		c1.addPrice(null, "minAge-maxAge", validPrice, validPrice);
		assertEquals(expectedPriceMatrixSize, c1.priceMatrix.size()); //no change since previous price invalid
		
		c1.addPrice("guestType2", "", validPrice, validPrice);
		assertEquals(expectedPriceMatrixSize, c1.priceMatrix.size()); //no change since previous price invalid
		
		c1.addPrice("guestType2", null, validPrice, validPrice);
		assertEquals(expectedPriceMatrixSize+1, c1.priceMatrix.size());
	}
}
