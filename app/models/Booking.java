package models;

import java.util.Date;

import javax.persistence.*;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * Draft of booking model class
 * 
 *
 */
@Entity
public class Booking extends Model {
	
	@Required
	@Id
	private String id;
	
	private Date dateOfBooking;
	
	private Date dayOfBookingStart;
	private Date dayOfBookingEnd;
	
	@Required
	private String userId;
	
	private String transactionId;
	
	//temporarily
	private String cabin;
	
	

	public Booking(Date dateOfBooking,
			String userId,
			Date dayOfBookingStart,
			Date dayOfBookingEnd,
			String cabin
			) {
		this.dateOfBooking = dateOfBooking;
		this.userId = userId;
		this.cabin = cabin;
		this.dayOfBookingEnd = dayOfBookingEnd;
		this.dayOfBookingStart = dayOfBookingStart;
		
	}
	
	public Booking(Date dateOfBooking, String userId) {
		this.dateOfBooking = dateOfBooking;
		this.userId = userId;	
	}
	
	 public String getId() {
		return id;
	}

	public Date getDateOfBooking() {
		return dateOfBooking;
	}
	
	public Date getDayOfBookingStart() {
		return this.dayOfBookingStart;
	}
	
	public Date getDayOfBookingEnd() {
		return this.dayOfBookingEnd;
	}
	
	public String getUserId() {
		return this.userId;
	}
	
	public String getCabin() {
		return cabin;
	}

	public void setCabin(String cabin) {
		this.cabin = cabin;
	}
	
	public static Booking create(Booking booking, Long cabinId) {
		booking.setCabin(cabinId +"");
		booking.save();
		return booking;
		
	}
	public static Finder<String,Booking> find = new Finder<String,Booking>(
		        String.class, Booking.class
		    ); 
}
