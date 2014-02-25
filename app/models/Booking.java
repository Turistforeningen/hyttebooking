package models;

import java.util.Date;

import javax.persistence.*;

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

	@OneToOne
	public Guest guest;

	@OneToOne
	public Payment payment;

	/** TEST **/
	public Booking(Long userId,
			Date dayOfBookingStart,
			Date dayOfBookingEnd) {
		this.guest = new Guest(userId);
		this.cabin = new SmallCabin(this);
		this.dateTo = dayOfBookingEnd;
		this.dateFrom = dayOfBookingStart;

	}
	/** END TEST **/

	/** Booking -> Cabin is many-to-one because there can be many bookings, but only one cabin
	 per. **/
	@ManyToOne
	public SmallCabin cabin; //TODO change this to SmallCabin when implemented
	
	@ManyToOne
	public Bed beds;

	public static Finder<String,Booking> find = new Finder<String,Booking>(
			String.class, Booking.class
			); 
}
