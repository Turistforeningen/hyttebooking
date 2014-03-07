package models;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Constraint;

import org.joda.time.DateTime;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

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

	public boolean isAvailable(DateTime fromDate, DateTime toDate) {

		for(Booking booking: bookings) //check through all bookings related to this bed and see if daterange overlap
		{
			DateTime fromDate2 = new DateTime(booking.dateFrom);
			DateTime toDate2 = new DateTime(booking.dateTo);

			if(isOverlap(fromDate, toDate, fromDate2, toDate2))
				return false;
		}
		

		return true;
	}

	/**
	 * Checks if two date ranges overlap. If any of the dates are the same it is regarded as overlap
	 * If there is a need to change so that toDate and fromDate2 (or vice versa) can overlap, then
	 * remove the .equals OR lines at the end
	 * @return true if dates overlap
	 */
	private boolean isOverlap(DateTime fromDate, DateTime toDate,
			DateTime fromDate2, DateTime toDate2) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		if((fromDate.isBefore(toDate2) && fromDate2.isBefore(toDate) ||
				((fmt.format(fromDate.toDate()).equals(fmt.format(fromDate2.toDate())) && 
						(fmt.format(fromDate.toDate()).equals(fmt.format(toDate2.toDate())))) || 
								fmt.format(fromDate2.toDate()).equals(fmt.format(toDate.toDate()))))) {
			return false;
		}
		return true;
	}

	/** Used for calendar lookup of specific date **/
	public boolean isAvailable(Date date) {
		// TODO Auto-generated method stub
		//if there exists a booking that overlaps date, return false
		return false;
	}
	public static Finder<Long,Bed> find = new Finder<Long,Bed>(
			Long.class, Bed.class
			); 
}