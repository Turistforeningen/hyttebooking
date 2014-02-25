package models;

import javax.persistence.*;

import play.data.validation.Constraints;

@Entity
public class SmallCabin extends Cabin {

	@OneToMany
	public Booking booking;
	
	public SmallCabin(Booking booking) {
		this.booking = booking;
	}
	
}
