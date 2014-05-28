package JSONFormatters;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import models.LargeCabin;
import models.SmallCabin;
import models.User;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.Json;
import play.test.WithApplication;

public class JsonHelperTest extends WithApplication {

	LargeCabin lc1;
	
	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
		User user1 = new User("q", "w", "John Doe");
		user1.save();
		User user2 = new User("w", "p", "Jane Doe");
		user2.save();
		User admin = new User("admin", "p", "admin");
		admin.isAdmin = true;
		admin.save();

		lc1 = new LargeCabin("Fjordheim", 10);
		lc1.addPrice("Voksen", "26 og opp", 400, 300, false);
		lc1.addPrice("Ungdom", "13-25", 300, 200, false);
		lc1.addPrice("Barn", "4-12", 200, 100, true);
		lc1.addPrice("Spedbarn", "0-4", 0, 0, true);
		lc1.save();
	}
	
	@Test
	public void testOutput() {
		JsonNode node = JsonHelper.getOkBooking();
		assertTrue("Error: node doesn't have guests"+node, node.has("guests"));
	}
	
}
