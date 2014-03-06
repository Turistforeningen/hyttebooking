package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Constraint;

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
	
	public static Finder<Long,Bed> find = new Finder<Long,Bed>(
			Long.class, Bed.class
			); 
}
