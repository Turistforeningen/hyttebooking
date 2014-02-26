package models;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.Constraints.Required;
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
	@Formats.DateTime(pattern="dd-MM-yyyy")
	public Date dateFrom;

	@Constraints.Required
	@Formats.DateTime(pattern="dd-MM-yyyy")
	public Date dateTo;
	
	public Date dateOfOrder;
	@ManyToOne
	@JsonIgnore
	public Guest guest;

	@OneToOne
	public Payment payment;

	/** TEST **/
	public Booking(Long userId,
			Date dayOfBookingStart,
			Date dayOfBookingEnd) {
		this.dateOfOrder = Calendar.getInstance().getTime();
		this.guest = Guest.find.byId(userId);
		this.cabin = new SmallCabin(this);
		this.dateTo = dayOfBookingEnd;
		this.dateFrom = dayOfBookingStart;

	}
	/** END TEST **/
	/** Booking -> Cabin is many-to-one because there can be many bookings, but only one cabin
	 per. **/
	@ManyToOne
	public SmallCabin cabin;
	
	@ManyToMany
	public List<Bed> beds;

	public static Finder<String,Booking> find = new Finder<String,Booking>(
			String.class, Booking.class
			); 
	
}
