package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.joda.time.DateTime;

import play.db.ebean.Model;
import utilities.Page;

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

	public abstract String getCabinUrl();
	
	public abstract boolean removePriceFromCabin(Long priceId);
	
	public abstract void addPriceFromCabin(Price price);
	
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
		if(page < 0 || pageSize < 0) 
			return null;
		
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
					.order("timeOfBooking desc")
					.findPagingList(pageSize)
					.getPage(page).getList();
			bookingPage.totalItems = new Integer(Booking.find.where().eq("beds.largeCabin", cabin).findRowCount());
			return bookingPage;
		}
		return null;
	}

	public static Page<Cabin> findAllCabins(int page, int pageSize) {
		if(page < 0 || pageSize < 0)
			return null;
		
		Page<Cabin> cabins = new Page<Cabin>();
		cabins.data = Cabin.find.where()
				.findPagingList(pageSize)
				.getPage(page).getList();
		cabins.totalItems = Cabin.find.findRowCount();
		return cabins;
	}
	
	
}
