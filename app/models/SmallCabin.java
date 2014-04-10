package models;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.joda.time.DateTime;

import com.avaje.ebean.Expr;

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
			
			DateTime fromDate2 = new DateTime(booking.dateFrom);
			DateTime toDate2 = new DateTime(booking.dateTo);
			
			if(utilities.DateHelper.isOverlap(fromDate, toDate, fromDate2, toDate2) && booking.status<Booking.CANCELLED)
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
				.gt("dateFrom", DateTime.now().toDate())
				.ne("status", Booking.CANCELLED)
				.findRowCount();
	}


	@Override
	public String getCabinUrl() {
		// TODO Auto-generated method stub
		return this.id + "?type=small&beds=hele";
	}	
}
