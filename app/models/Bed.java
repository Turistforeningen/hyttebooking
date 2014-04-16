package models;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import org.joda.time.DateTime;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class Bed extends Model  {

	@Id
	public Long id;

	@Constraints.Required
	@ManyToOne(cascade = CascadeType.ALL)
	public LargeCabin largeCabin;

	@ManyToMany( cascade = CascadeType.ALL)
	public List<Booking> bookings = new ArrayList<Booking>();
	
	public void addBooking(Booking b) {
		if (this.bookings == null) {
			bookings = new ArrayList<Booking>();
		}
		bookings.add(b);
}

	/**
	 * Used for checking if bed is available for given date range
	 * @return false if any of the bookings for this bed overlaps with daterange fromDate-toDate
	 */
	public boolean isAvailable(DateTime fromDate, DateTime toDate) {

		for(Booking booking: bookings) //check through all bookings related to this bed and see if daterange overlap
				if(utilities.DateHelper.isOverlap(fromDate, toDate, booking.dateFrom, booking.dateTo) && booking.status<Booking.CANCELLED)
					return false;
	
		return true;
	}

	public static Finder<Long,Bed> find = new Finder<Long,Bed>(
			Long.class, Bed.class
			); 
	
	public void delete() {
		this.largeCabin = null;
		this.update();
		super.delete();
	}
}
