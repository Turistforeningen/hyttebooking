package models;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	
	@ManyToOne
	public User user;
	
	private String transactionId;
	
	//temporarily
	private String cabin;
	private int nrOfPersons;
	

	public Booking(Date dateOfBooking,
			User userId,
			Date dayOfBookingStart,
			Date dayOfBookingEnd,
			String cabin
			) {
		this.dateOfBooking = dateOfBooking;
		this.user = userId;
		this.cabin = cabin;
		this.dayOfBookingEnd = dayOfBookingEnd;
		this.dayOfBookingStart = dayOfBookingStart;
		
	}
	
	public Booking(Date dateOfBooking, User userId) {
		this.dateOfBooking = dateOfBooking;
		this.user = userId;	
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
	
	public User getUserId() {
		return this.user;
	}
	
	public String getCabin() {
		return cabin;
	}

	public void setCabin(String cabin) {
		this.cabin = cabin;
	}
	
	public void setCabin(int persons) {
		this.nrOfPersons = persons;
	}
	
	public static Booking create(Booking booking, Long cabinId) {
		booking.setCabin(cabinId +"");
		booking.save();
		return booking;
		
	}
	
	public static List<Booking> findByUser(User user) {
		Finder<Long, Booking> finder = new Finder<Long, Booking>(Long.class, Booking.class);
		return finder.where().eq("user", user).findList();
	}

	public static Finder<String,Booking> find = new Finder<String,Booking>(
			String.class, Booking.class
			); 
}
