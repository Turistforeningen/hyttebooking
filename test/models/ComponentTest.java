package models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

import java.util.ArrayList;
import java.util.List;

import models.Bed;
import models.Booking;
import models.Cabin;
import models.LargeCabin;
import models.SmallCabin;
import models.User;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import play.test.WithApplication;
import utilities.Page;

public class ComponentTest extends WithApplication {
	
	private User u;
	
	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
		u = new User("u@u.no", "password1", "ComponentTestUser");
		u.save();
	}
	
	@Test
	public void deleteBedWontDeleteBooking() {
		
		LargeCabin cabin = new LargeCabin("treet", 10);
		cabin.save();
		Long id = cabin.id;
		cabin = (LargeCabin)Cabin.find.byId(id);
		List<Bed> beds = cabin.beds;
		Booking book = Booking.createBooking(u.id, RDate.fDt, RDate.fDt.plusDays(4), id, beds);
		
		long bId = book.id;
		beds.get(0).delete();
		assertNotNull(Booking.find.byId(bId));
	}
	
	@Test
	public void deleteBookingWontDeleteBed() {
		
		LargeCabin cabin = new LargeCabin("t", 10);
		cabin.save();
		Long id = cabin.id;
		cabin = (LargeCabin)Cabin.find.byId(id);
		List<Bed> beds = cabin.beds;
		int nr = beds.size();
		long bId = beds.get(0).id;
		Booking book = Booking.createBooking(u.id, RDate.fDt, RDate.fDt.plusDays(4), id, beds);
		
		long bookId = book.id;
		book.delete();
		
		LargeCabin cabin2 = (LargeCabin)(LargeCabin.find.byId(id));
		assertEquals(nr, cabin.beds.size());
		//System.out.println((nr == cabin.beds.size())+ " : true");
		assertNull(Booking.find.byId(new Long(bookId)));
		assertNotNull(Bed.find.byId(new Long(bId)));
		//System.out.println((Booking.find.byId(new Long(bookId)) == null)+ ": book er null nå");
	}
	
	@Test
	/**
	 * Delete
	 */
	public void deleteBedsWontDeleteCabin() {
		
		LargeCabin cabin = new LargeCabin("jordet", 10);
		cabin.save();
		long id = cabin.id;
		cabin = (LargeCabin)Cabin.find.byId(id);
		assertNotNull(cabin);
		//System.out.println(cabin.name);
		long bedId = cabin.beds.get(0).id;
		Bed b = Bed.find.byId(bedId);
		//two lines below makes sure that cabin is not deleted apparently.
		b.largeCabin = null;
		b.update();
		//end
		b.delete();
		
		assertNotNull(Cabin.find.byId(id));
		//System.out.println((Bed.find.byId(bedId)) + " : bed is null");
		assertNull(Bed.find.byId(bedId));
	}
	
	@Test
	/**
	 * Creates a booking and tests if the booking adds the beds to itself
	 */
	public void createBookingWillAddBeds() {
		LargeCabin cabin = new LargeCabin("p", 10);
		cabin.save();
		Bed one = new Bed();
		one.save();
		Bed two = new Bed();
		two.save();
		List<Bed> beds = new ArrayList<Bed>();
		beds.add(cabin.beds.get(0));
		beds.add(cabin.beds.get(1));
		Booking b1 = Booking.createBooking(u.id, RDate.fDt, RDate.fDt.plusDays(4), cabin.id, beds);
		
		Long id = b1.id;
		
		b1 = Booking.find.byId(id);
		List<Bed> b= b1.beds;
		assertNotEquals(b.size(), 0);
		//System.out.println(b.size() +" bed size");
	}
	
	@Test
	//TODO already tested customer-booking linking in Payment, consider removing
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
		Booking b1 = Booking.createBooking(u.id, RDate.fDt, RDate.fDt.plusDays(4), cabin.id, beds);
		
		Long id = b1.id;
		
		b1 = Booking.find.byId(id);
		List<Bed> b= b1.beds;
		assertNotEquals(b.size(), 0);
		//System.out.println(b.size() +" bed size");
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
	public void deleteCabinWillDeleteBeds() {
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
			//System.out.println("Bed should not be null: " + Bed.find.byId(i));
			assertNull(Bed.find.byId(i));
		}
	}
	
	@Test
	public void cancelledBookingWillNotBeReturnedInOrderHistory() {
		
		SmallCabin sCabin = new SmallCabin("Hei");
		sCabin.save();
		User user = new User("q@t","w", "t");
		user.save();
		Booking b = Booking.createBooking(user.id, RDate.fDt, RDate.fDt.plusDays(4), sCabin.id, null);
		
		int bookingSizeForUser = Booking.find.where().eq("user", user).findList().size();
		b.status = Booking.CANCELLED;
		b.save();
		int bookingNewSize = Booking.getBookingPageByUser(user, 0, 10).totalItems;
		//System.out.println(bookingSizeForUser + " --------");
		//System.out.println(bookingNewSize + " --------");
		assertNotEquals(bookingSizeForUser, bookingNewSize);
	}
	
	/*
	/** Tests that findAllBookingsForCabinGivenDate in Cabin returns correct lists within given daterange 
	@Test
	public void TestFindAllBookingsForCabinGivenDate() {
		SmallCabin sCabin = new SmallCabin("ErBookinglistHytte");
		sCabin.save();
		Booking b = Booking.createBooking(u.id, RDate.fDt, RDate.fDt.plusDays(5), sCabin.id, null);
		
		List<Booking> bookingsShouldBeEmpty = sCabin.findAllBookingsForCabinGivenDate(sCabin.id, RDate.fDt.plusWeeks(1), RDate.fDt.plusWeeks(2));
		List<Booking> bookingsNotEmpty = sCabin.findAllBookingsForCabinGivenDate(sCabin.id, RDate.fDt.plusDays(5), RDate.fDt.plusDays(15));
				
		assertTrue(bookingsShouldBeEmpty.isEmpty());
		assertFalse(bookingsNotEmpty.isEmpty()); 
	}
	*/
	
	@Test
	//TODO maybe replace
	public void TestMakeSeveralBookingsForDifferentCabins() {
		
		User user1 = new User("tt", "ww", "John Doe");
		user1.save();
		
		LargeCabin lc1 = new LargeCabin("fh", 10);
		   lc1.save();
		   
		   LargeCabin lc2 = new LargeCabin("pt", 20);
		   lc2.save();

		   Cabin[] cabins = {lc1, lc2};
		   
		   
		   	int cabinSize = cabins.length;
			for ( int i = 0; i<6; i++) {
				List<Bed> beds = null;
				Cabin currentCabin = cabins[i%cabinSize];
				
			
				
				DateTime fromDate = DateTime.now().plusDays(2);
				DateTime toDate = DateTime.now().plusDays(4);
				
				if(currentCabin instanceof LargeCabin) {
					beds = ((LargeCabin)currentCabin).beds;
					
				}
				else {
					beds =null;
				}
				
				Booking b= Booking.createBooking(user1.id, fromDate, toDate, currentCabin.id, beds );
				
				
			}
			
			Page<Booking> bookings = Booking.getBookingPageByUser(user1, 0, 6);
			//System.out.println(bookings.data.size());
			for(Booking b : bookings.data) {
				//System.out.println(b.getCabin().name + "-----------------------");
				assertNotNull(b.getCabin().name);
			}
			
			//Conclusion: b.update in Booking addBed a bad idea.
	}
	
}
