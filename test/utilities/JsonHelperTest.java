package utilities;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import models.LargeCabin;
import models.SmallCabin;
import models.User;

import org.junit.Before;
import org.junit.Test;

import controllers.JsonHelper;
import play.test.WithApplication;

public class JsonHelperTest extends WithApplication {

	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
		User user1 = new User("q", "w", "John Doe");
		user1.save();
		User user2 = new User("w", "p", "Jane Doe");
		user2.save();
		User admin = new User("admin", "p", "admin");
		admin.admin = true;
		admin.save();

		User[] us = {user1, user2};
		int userSize = us.length;

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
	public void testOutput() {
		System.out.println("########### OK BOOKING HERE!!!!!!!!!\n"+JsonHelper.getOkBooking().asText());
		assertTrue(false);
	}
	
}
