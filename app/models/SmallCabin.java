package models;

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
}
