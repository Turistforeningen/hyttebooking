package models;

import java.util.Date;

import javax.persistence.*;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * Revision of booking model
 * v.01 (Jama) Changes made:
 * 	1. private properties changed to public, according to play this insanity is the usual MO
 * 	2. added stricter constraints and formatting requirements to dates
 * 	3. removed getters and setters due to point 1
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

	@Required
	public String userId;

	//do we need this? TODO
	public String transactionId;


	/** TEST **/
	public Booking(String userId,
			Date dayOfBookingStart,
			Date dayOfBookingEnd,
			SmallCabin cabin
			) {
		this.userId = userId;
		this.cabin = cabin;
		this.dateTo = dayOfBookingEnd;
		this.dateFrom = dayOfBookingStart;

	}
	/** END TEST **/

	/** Booking -> Cabin is many-to-one because there can be many bookings, but only one cabin
	 per. **/
	@ManyToOne
	public SmallCabin cabin; //TODO change this to SmallCabin when implemented

	public static Finder<String,Booking> find = new Finder<String,Booking>(
			String.class, Booking.class
			); 
}
