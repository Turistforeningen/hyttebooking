package controllers;

import org.junit.Before;
import org.junit.Test;

import play.mvc.Result;
import play.test.WithApplication;
import static org.junit.Assert.*;
import static play.test.Helpers.GET;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.route;

public class RouteTests extends WithApplication {
	
	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
	}

	 @Test
	 public void rootRoute() {
		 Result res = route(fakeRequest(GET, "/dev"));
		 assertNotNull(res);
	 }
	 
	 @Test
	 public void badRoute() {
		 Result res = route(fakeRequest(GET, "/bad"));
		 assertNull(res);
	 }
}
