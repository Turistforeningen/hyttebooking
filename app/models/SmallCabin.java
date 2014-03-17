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

	
	public boolean isAvailable(Date date) {
		// TODO Auto-generated method stub
		return false;
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
}
