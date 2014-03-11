package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.joda.time.DateTime;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Expression;

import play.db.ebean.Model;
import utilities.Page;
import views.html.play20.book;

/**
 * Abstract superclass for SmallCabin and LargeCabin. This is taken straight from JPA inheritance
 * pages (http://en.wikibooks.org/wiki/Java_Persistence/Inheritance) and seems to be most popular
 * solution.
 * @author Jama
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class Cabin extends Model {

	@Id
	public Long id;

	public String name;

	public Cabin(String name) {
		this.name = name;	
	}

	/**
	 * Get the number of active bookings for cabin.
	 * Used primarily by flexjson serialization.
	 * @return int - number of active bookings
	 */
	public abstract int getNrActiveBookings();

	public abstract String getcabinType();

	public abstract String getNrOfBeds();

	public static Finder<Long, Cabin> find = new Finder<Long, Cabin>(Long.class, Cabin.class);


	/**
	 * Returns a page of bookings for a given cabin. What subset of bookings and size
	 * of list are decided by the page and pageSize arguments.
	 * 
	 * @param cabinId
	 * @param page 
	 * @param pageSize
	 * @return
	 */
	public static Page<Booking> findAllBookingsForCabin(Long cabinId, int page, int pageSize) {
		Cabin cabin = Cabin.find.byId(cabinId);
		Page<Booking> bookingPage = new Page<Booking>();
		if(cabin instanceof SmallCabin) {

			bookingPage.data = Booking.find.where()
					.eq("smallCabin", cabin)
					.findPagingList(pageSize)
					.getPage(page).getList();
			bookingPage.totalItems = new Integer(((SmallCabin)cabin).bookings.size());
			return bookingPage;

		}
		else if(cabin instanceof LargeCabin) {
			bookingPage.data = Booking.find.where()
					.eq("beds.largeCabin", cabin)
					.findPagingList(pageSize)
					.getPage(page).getList();
			bookingPage.totalItems = new Integer(Booking.find.where().eq("beds.largeCabin", cabin).findRowCount());
			return bookingPage;
		}
		return null;
	}

	/**
	 * Returns all bookings that overlap with given startDate and endDate (used in dynamic calendar display)
	 * If 01-01-2010 
	 * @param cabinId
	 * @param startDate
	 * @param endDate
	 * @return A list of all bookings found within given cabinId within startDate-endDate
	 */
	public static List<Booking> findAllBookingsForCabinGivenDate(long cabinId, DateTime startDate, DateTime endDate)
	{
		Cabin cabin = Cabin.find.byId(cabinId);
		List<Booking> bookings = new ArrayList<Booking>();

		if(cabin instanceof SmallCabin) {
			bookings = Booking.find.where()
					.eq("smallCabin", cabin) //TODO optimization consider optimizing query only for specified dates
					.findList();
		
		} else if(cabin instanceof LargeCabin) {
			bookings = Booking.find.where()
					.eq("beds.largeCabin", cabin)
					.findList(); //TODO optimization consider optimizing query only for specified dates
		}
		
		List<Booking> rBookings = new ArrayList<Booking>();
		for(Booking b: bookings) { //we only want dates that overlap within given range
			if(utilities.DateHelper.isOverlap(new DateTime(b.dateFrom), new DateTime(b.dateTo), startDate, endDate))
					rBookings.add(b);
		}
			return rBookings; //TODO
	}

	public static Page<Cabin> findAllCabins(int page, int pageSize) {
		Page<Cabin> cabins = new Page<Cabin>();
		cabins.data = Cabin.find.where()
				.findPagingList(pageSize)
				.getPage(page).getList();
		cabins.totalItems = Cabin.find.findRowCount();
		return cabins;
	}
}
