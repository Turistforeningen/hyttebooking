package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

/**
 * Revision of booking model
 * v.02 (Jama) Changes made:
 * 	1. Added @ManyToOne to Beds
 */
@Entity
public class Booking extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	public Long id;
	
	@Constraints.Required
	@Formats.DateTime(pattern="yyyy-MM-dd")
	public Date dateFrom;

	@Constraints.Required
	@Formats.DateTime(pattern="yyyy-MM-dd")
	public Date dateTo;
	
	/** Time of order contains information about when the order took place in greater detail **/
	public Long timeOfBooking;
	
	@ManyToOne
	@JsonIgnore
	public Guest guest;

	@OneToOne
	public Payment payment;
	
	
	@ManyToOne
	@JsonIgnore
	public SmallCabin cabin;
	
	@ManyToMany(mappedBy = "bookings",cascade = CascadeType.ALL)
	public List<Bed> beds = new ArrayList<Bed>();
	
	public void addBed(Bed b) {
		beds.add(b);
		
	}
	public Cabin getCabin() {
		if(beds.size() != 0) {
			System.out.println(beds.size());
			System.out.println(beds.get(0).largeCabin);
			return beds.get(0).largeCabin;
		}
		else {
			return cabin;
		}
	}
	/** TEST **/
	public Booking(Long userId,
			Date dayOfBookingStart,
			Date dayOfBookingEnd,
			Long cabinId,
			List<Bed> beds) {
		this.timeOfBooking = Calendar.getInstance().getTimeInMillis();
		this.guest = Guest.find.byId(userId);
		Cabin cabin = Cabin.find.byId(cabinId);
		
		if(cabin instanceof SmallCabin) {
			this.cabin = (SmallCabin)cabin;
		}
		else {
			//skal ikke legge til alle beds.
			
			for(Bed bed: beds) {
				addBed(bed);
				
				
			}
			
		}
		
		this.dateTo = dayOfBookingEnd;
		this.dateFrom = dayOfBookingStart;

	}
	/** END TEST **/

	public static Finder<Long,Booking> find = new Finder<Long,Booking>(
			Long.class, Booking.class
			); 
	
	public String toString() {
		return "id: " + this.id + " cabin" + this.cabin.name;
	}
}
