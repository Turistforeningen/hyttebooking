package controllers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.*;

import org.junit.*;

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
	
	
}
