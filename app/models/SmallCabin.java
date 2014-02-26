package models;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import play.data.validation.Constraints;

@Entity
public class SmallCabin extends Cabin {

	@OneToMany
	public List<Booking> bookings;
	
	public SmallCabin(Booking booking) {
		this.bookings.add(booking);
	}

	@Override
	public boolean isAvailable(Date date) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
