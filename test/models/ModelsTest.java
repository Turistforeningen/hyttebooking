package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.*;

import org.joda.time.DateTime;
import org.junit.*;

import com.avaje.ebean.Ebean;

import static org.junit.Assert.*;
import play.Logger;
import play.mvc.Result;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class ModelsTest extends WithApplication{
	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
	}
	
	@Test
	public void DeleteBookingWontDeleteBed() {
		
		LargeCabin cabin = new LargeCabin("t", 10);
		cabin.save();
		Long id = cabin.id;
		cabin = (LargeCabin)Cabin.find.byId(id);
		List<Bed> beds = cabin.beds;
		int nr = beds.size();
		long bId = beds.get(0).id;
		Booking book = Booking.createBooking(new Long(1), new Date(), new Date(), id, beds);
		
		long bookId = book.id;
		book.delete();
		
		LargeCabin cabin2 = (LargeCabin)(LargeCabin.find.byId(id));
		assertEquals(nr, cabin.beds.size());
		System.out.println((nr == cabin.beds.size())+ " : true");
		assertNull(Booking.find.byId(new Long(bookId)));
		assertNotNull(Bed.find.byId(new Long(bId)));
		System.out.println((Booking.find.byId(new Long(bookId)) == null)+ ": book er null nå");
	}
	
	
	@Test
	public void checkBookingCustomerRelationship() {
		LargeCabin cabin = new LargeCabin("p", 10);
		cabin.save();
		Bed one = new Bed();
		one.save();
		Bed two = new Bed();
		two.save();
		List<Bed> beds = new ArrayList<Bed>();
		beds.add(cabin.beds.get(0));
		beds.add(cabin.beds.get(1));
		Booking b1 = Booking.createBooking(new Long(1), new Date(), new Date(),cabin.id, beds);
		
		Long id = b1.id;
		
		b1 = Booking.find.byId(id);
		List<Bed> b= b1.beds;
		assertNotEquals(b.size(), 0);
		System.out.println(b.size() +" bed size");
		
		
		
	}
	
	@Test
	public void checkBedsPersistCabinOwnerRelationShip() {
		//showed that cascade were needed when defining onetomany etc
		LargeCabin cabin = new LargeCabin("steinen", 10);
		cabin.save();
		Long id = cabin.id;
		cabin = (LargeCabin)Cabin.find.byId(id);
		List<Bed> b = cabin.beds;
		assertNotNull(b.get(0).largeCabin);
		
	}
	
	@Test
	public void TestCabinDeleteToSeeIfBedsAreDeleted() {
		
		LargeCabin cabin = new LargeCabin("bladet", 10);
		cabin.save();
		long id = cabin.id;
		cabin = (LargeCabin)Cabin.find.byId(id);
		long bedId = cabin.beds.get(0).id;
		cabin.delete();
		System.out.println("cabin deleted?");
		assertNull(Cabin.find.byId(id));
		System.out.println("bedDeleted");
		assertNull(Bed.find.byId(bedId));
		
	}
	
	@Test
	public void TestBedsDeletedDoesNotDeleteCabin() {
		
		LargeCabin cabin = new LargeCabin("jordet", 10);
		cabin.save();
		long id = cabin.id;
		cabin = (LargeCabin)Cabin.find.byId(id);
		assertNotNull(cabin);
		System.out.println(cabin.name);
		long bedId = cabin.beds.get(0).id;
		Bed b = Bed.find.byId(bedId);
		//two lines below makes sure that cabin is not deleted apparently.
		b.largeCabin = null;
		b.update();
		//end
		b.delete();
		
		assertNotNull(Cabin.find.byId(id));
		System.out.println((Bed.find.byId(bedId)) + " : bed is null");
		assertNull(Bed.find.byId(bedId));
		
		
	}
	
	@Test
	public void DeleteBedWontDeleteBooking() {
		
		LargeCabin cabin = new LargeCabin("treet", 10);
		cabin.save();
		Long id = cabin.id;
		cabin = (LargeCabin)Cabin.find.byId(id);
		List<Bed> beds = cabin.beds;
		Booking book = Booking.createBooking(new Long(1), new Date(), new Date(), id, beds);
		
		long bId = book.id;
		beds.get(0).delete();
		assertNotNull(Booking.find.byId(bId));
	}
	
	@Test
	public void DeleteCabinWillDeleteBedsCheck() {
		LargeCabin cabin = new LargeCabin("fjorden", 10);
		cabin.save();
		Long id = cabin.id;
		cabin = (LargeCabin)Cabin.find.byId(id);
		List<Bed> beds = cabin.beds;
		ArrayList<Long> ids = new ArrayList<Long>(); 
		for(Bed b: beds) {
			ids.add(new Long(b.id.longValue()));
		}
		cabin.delete();
		
		for(Long i : ids) {
			System.out.println("Bed should not be null: " + Bed.find.byId(i));
			assertNull(Bed.find.byId(i));
		}
	}
	
	@Test
	public void CancelledBookingWillNotBeReturnedInOrderHistory() {
		
		SmallCabin sCabin = new SmallCabin("Hei");
		sCabin.save();
		User user = new User("q@t","w", "t");
		user.save();
		Booking b = Booking.createBooking(user.id, new Date(), new Date(), sCabin.id, null);
		
		int bookingSizeForUser = Booking.find.where().eq("user", user).findList().size();
		b.status = Booking.CANCELLED;
		b.save();
		int bookingNewSize = Booking.getBookingPageByUser(user, 0, 10).totalItems;
		System.out.println(bookingSizeForUser + " --------");
		System.out.println(bookingNewSize + " --------");
		assertNotEquals(bookingSizeForUser, bookingNewSize);
	
	}
	
	/** Tests that findAllBookingsForCabinGivenDate in Cabin returns correct lists within given daterange **/
	@Test
	public void TestFindAllBookingsForCabinGivenDate() {
		SmallCabin sCabin = new SmallCabin("ErBookinglistHytte");
		sCabin.save();
		User user = new User("q@t","w", "t");
		user.save();
		Booking b = Booking.createBooking(user.id, DateTime.now().toDate(), DateTime.now().plusDays(5).toDate(), sCabin.id, null);
		
		List<Booking> bookingsShouldBeEmpty = sCabin.findAllBookingsForCabinGivenDate(sCabin.id, DateTime.now().plusWeeks(1), DateTime.now().plusWeeks(2));
		List<Booking> bookingsNotEmpty = sCabin.findAllBookingsForCabinGivenDate(sCabin.id, DateTime.now().plusDays(5), DateTime.now().plusDays(15));
				
		assertTrue(bookingsShouldBeEmpty.isEmpty());
		assertFalse(bookingsNotEmpty.isEmpty()); 
	}
	
	@Test
	public void TestWithinDate() {
		DateTime a = new DateTime().now();
		DateTime b = new DateTime().now().plus(5);
		
		DateTime p1 = new DateTime().now().plus(2); //within = true
		DateTime p2 = new DateTime().now().plus(5); //within = true
		DateTime p3 = new DateTime().now(); //within = true
		DateTime p4 = new DateTime().now().plus(6); //within = false
		DateTime p5 = new DateTime().now().minus(1); //within = false
		
		assertTrue(utilities.DateHelper.withinDate(p1, a, b));
		assertTrue(utilities.DateHelper.withinDate(p2, a, b));
		assertTrue(utilities.DateHelper.withinDate(p3, a, b));
		assertFalse(utilities.DateHelper.withinDate(p4, a, b));
		assertFalse(utilities.DateHelper.withinDate(p5, a, b));
	}
	
	@Test
	public void TestDaysBetween() {
		int diff = 5;
		
		DateTime a = new DateTime().now();
		DateTime b = new DateTime().now().plusDays(diff);

		System.out.println(a);
		System.out.println(b);
		assertEquals(5, utilities.DateHelper.daysBetween(a, b));
	}
}
