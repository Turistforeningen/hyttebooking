package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.joda.time.DateTime;

import play.data.validation.Constraints;

@Entity
@DiscriminatorValue("SMALL_CABIN")
public class SmallCabin extends Cabin {
	
	@Constraints.Required
	public double memberPrice;
	@Constraints.Required
	public double nonMemberPrice;
	
	@OneToMany
	public List<Booking> bookings;
	
	public SmallCabin(String name) {
		super(name);
	}

	
	public boolean isAvailable(DateTime fromDate, DateTime toDate) {
		for(Booking booking: this.bookings) {
			if(utilities.DateHelper.isOverlap(fromDate, toDate, booking.dateFrom, booking.dateTo) && booking.status<Booking.CANCELLED)
				return false;
		}
		
		return true;
	}


	@Override
	public String getcabinType() {
		return "small";
	}


	@Override
	public String getNrOfBeds() {
		return null;
	}


	@Override
	public int getNrActiveBookings() {
		return Booking.find
				.where()
				.eq("smallCabin", this)
				.gt("dateFrom", DateTime.now())
				.ne("status", Booking.CANCELLED)
				.findRowCount();
	}


	@Override
	public String getCabinUrl() {
		// TODO Auto-generated method stub
		return this.id + "?type=small&beds=hele";
	}	
	
	/**
	 * Returns all bookings that overlap with given startDate and endDate (used in dynamic calendar display) that are NOT cancelled
	 * @return A list of all bookings found within given cabinId within startDate-endDate
	 */
	public static List<Booking> findAllBookingsForCabinGivenDate(long cabinId, DateTime fromDate, DateTime toDate)
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
		for(Booking b: bookings) {
			if(b.status<Booking.CANCELLED) { //if booking isn't cancelled or timedout
				if(utilities.DateHelper.isOverlap(b.dateFrom, b.dateTo, fromDate, toDate)) 
					rBookings.add(b);
			}
		}
		return rBookings;
	}
}
