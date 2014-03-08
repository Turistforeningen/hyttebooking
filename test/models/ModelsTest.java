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
		Booking book = new Booking(new Long(1), new Date(), new Date(), id, beds);
		book.save();
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
		Booking b1 = new Booking(new Long(1), new Date(), new Date(),cabin.id, beds);
		b1.save();
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
		Bed b = cabin.beds.get(0);
		b.delete();
		System.out.println(((LargeCabin)Cabin.find.byId(id)) + " : cabin should not be null");
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
		Booking book = new Booking(new Long(1), new Date(), new Date(), id, beds);
		book.save();
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
		Booking b = new Booking(user.id, new Date(), new Date(), sCabin.id, null);
		b.save();
		int bookingSizeForUser = Booking.find.where().eq("user", user).findList().size();
		b.status = Booking.CANCELLED;
		b.save();
		int bookingNewSize = Booking.getBookingPageByUser(user, 0, 10).totalItems;
		System.out.println(bookingSizeForUser + " --------");
		System.out.println(bookingNewSize + " --------");
		assertNotEquals(bookingSizeForUser, bookingNewSize);
	
	}
	
	@Test
	public void CancelledBookingWillReleaseBedsItHolds() {
	
		LargeCabin cabin = new LargeCabin("test", 2);
		cabin.save();
		List<Bed> beds = new ArrayList<Bed>();
		beds.add(cabin.beds.get(0));
		long bid = beds.get(0).id;
		Booking b = new Booking(new Long(1), DateTime.now().toDate(), DateTime.now().toDate(), cabin.id, beds);
		b.save();
		long id = b.id;
		
		
		b = Booking.find.byId(id);
		b.status = Booking.CANCELLED;
		b.removeAllBeds();
		b.update();
		
		
		
		assertNotNull(Bed.find.byId(bid));
		Bed bed = Bed.find.byId(bid);
		System.out.println(bed.bookings.get(0).user.id + "id to user");
		assertNull(bed.bookings.get(0));
	}
	
}
