package controllers;

import models.LargeCabin;
import models.SmallCabin;
import models.User;

import org.junit.*;

import static org.junit.Assert.*;
import play.mvc.Result;
import play.test.FakeRequest;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class AdminControllerTest extends WithApplication  {

	User user;
	User admin;
	final static String authToken = "X-AUTH-TOKEN";
	
	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
		
		this.user = new User("q", "w", "John Doe");
		this.user.save();
		this.admin = new User("admin", "p", "admin");
		this.admin.admin = true;
		this.admin.save();
		
		LargeCabin lc1 = new LargeCabin("Fjordheim", 10);
		lc1.addPrice("Voksen", "26 og opp", 400, 300);
		lc1.addPrice("Ungdom", "13-25", 300, 200);
		lc1.addPrice("Barn", "4-12", 200, 100);
		lc1.addPrice("Spedbarn", "0-4", 0, 0);
		lc1.save();

		LargeCabin lc2 = new LargeCabin("Peterstun", 20);
		lc2.addPrice("Voksen", "26 og opp", 450, 350);
		lc2.addPrice("Ungdom", "13-25", 350, 250);
		lc2.addPrice("Barn", "4-12", 250, 150);
		lc2.addPrice("Spedbarn", "0-4", 0, 0);
		System.out.println(lc2.priceMatrix.size() + "size på den greia");
		lc2.save();

		SmallCabin sc1 = new SmallCabin("Helfjord");
		sc1.setPrice("Hele", " ", 1000, 800);
		sc1.save();

		SmallCabin sc2 = new SmallCabin("Fjordlistølen");
		sc2.setPrice("Hele", " ", 1000, 800);
		sc2.save();
	}
	
	@Test
	public void testAdminCancelBooking() {
		//Normal user can't delete cabin as admin
		FakeRequest fkRequest = new FakeRequest(DELETE, "/api/admin/bookings/1");
		fkRequest.withHeader(authToken, user.createToken());
		
		Result resBad = route(fkRequest);
		assertEquals(UNAUTHORIZED, status(resBad));

		//DELETE  /api/admin/bookings/:id
		fkRequest = new FakeRequest(DELETE, "/api/admin/bookings/1");
		fkRequest.withHeader(authToken, admin.createToken());
		
		Result resOk = route(fkRequest);
		assertEquals(OK, status(resOk));
	}
	
	@Test
	/**
	 * Test that unauthorized user can't get cabins
	 */
	public void testGetCabins() {
		//GET     /api/admin/cabins 
		//TODO maybe add query parameters to the route here? vvv
		FakeRequest fkRequest = new FakeRequest(GET, "/api/admin/cabins");
		fkRequest.withHeader(authToken, user.createToken());
		
		Result resBad = route(fkRequest);
		assertEquals(UNAUTHORIZED, status(resBad));
		
		//GET USERS AS ADMIN
		//TODO maybe add query parameters to the route here? vvv
		fkRequest = new FakeRequest(GET, "/api/admin/cabins");
		fkRequest.withHeader(authToken, admin.createToken());
		
		Result resOk = route(fkRequest);
		assertEquals(OK, status(resOk));
	}
	
	@Test
	public void testGetCabinDetails() {
		//GET     /api/admin/cabins/:id  
		//TODO maybe add query parameters to the route here? vvv
		FakeRequest fkRequest = new FakeRequest(GET, "/api/admin/cabins/1");
		fkRequest.withHeader(authToken, user.createToken());
		
		Result resBad = route(fkRequest);
		assertEquals(UNAUTHORIZED, status(resBad));
		
		//GET CABIN DETAILS AS ADMIN
		//TODO maybe add query parameters to the route here? vvv
		fkRequest = new FakeRequest(GET, "/api/admin/cabins/1");
		fkRequest.withHeader(authToken, admin.createToken());
		
		Result resOk = route(fkRequest);
		assertEquals(OK, status(resOk));
	}
	
	@Test
	/**
	 * Test that unauthorized user cannot submit cabin
	 * Test that authorized admin can submit cabin
	 * Test that submitted cabin enters pool of cabins
	 */
	public void testSubmitCabin() {
		//POST    /api/admin/cabins 
		FakeRequest fkRequest = new FakeRequest(POST, "/api/admin/cabins");
		fkRequest.withHeader(authToken, user.createToken());
		fkRequest.withJsonBody(JsonHelper.getOkBooking());
		
		Result resBad = route(fkRequest);
		assertEquals(UNAUTHORIZED, status(resBad));
		
		//SUBMIT CABIN AS ADMIN
		fkRequest = new FakeRequest(POST, "/api/admin/cabins");
		fkRequest.withHeader(authToken, admin.createToken());
		fkRequest.withJsonBody(JsonHelper.getOkBooking());
		
		Result resOk = route(fkRequest);
		assertEquals(OK, status(resOk));
	}
}
