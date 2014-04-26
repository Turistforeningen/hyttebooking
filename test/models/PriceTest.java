package models;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

import org.junit.Before;
import org.junit.Test;

import play.test.WithApplication;

public class PriceTest extends WithApplication {

	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
	}
	
	@Test
	/**
	 * Tests that prices cannot be negative
	 */
	public void testPriceConstructor() {
		//make a price matrix
		//test that it doesnt accept negative values for price
		//TODO
	}
}
