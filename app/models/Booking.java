package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.joda.time.DateTime;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Expression;
import com.fasterxml.jackson.annotation.JsonIgnore;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class Booking extends Model {
	public static Integer CANCELLED = new Integer(2);
	public static Integer PAID = new Integer(1);
	public static Integer BOOKED = new Integer(0);
	@Id
	public Long id;
	
	@Constraints.Required
	@Formats.DateTime(pattern="yyyy-MM-dd")
	public Date dateFrom;

	@Constraints.Required
	@Formats.DateTime(pattern="yyyy-MM-dd")
	public Date dateTo;
	
	/** Time of order contains information about when the booking took place as timestamp long **/
	public Long timeOfBooking;
	
	@Constraints.Required
	@OneToMany(mappedBy="booking", cascade = CascadeType.ALL, orphanRemoval=true) //you were here a
	public List<Guest> guests;
	
	@Constraints.Required
	@ManyToOne
	public User user;
	
	public Integer status = new Integer(0);
	
	@OneToOne
	public Payment payment;
	
	@ManyToOne
	@JsonIgnore
	public SmallCabin smallCabin;
	
	@ManyToMany(mappedBy = "bookings",cascade = CascadeType.ALL)
	public List<Bed> beds = new ArrayList<Bed>();
	
	public void addBed(Bed b) {
		beds.add(b);
		
	}
	
	
	/**
	 * Will return a smallcabin or a bigcabin depending on booking type.
	 * 
	 * @return Cabin
	 */
	public Cabin getCabin() {
		if(beds.size() != 0) {
			return beds.get(0).largeCabin;
		}
		else {
			return smallCabin;
		}
	}
	
	public boolean isAbleToCancel() {
		if(DateTime.now().plusDays(7).isAfter(this.dateTo.getTime())) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public String getNrOfBeds() {
		if(beds.size() == 0) {
			return null;
		}
		else {
			return beds.size() +"";
		}
	}
	public Booking() {
		this.timeOfBooking = Calendar.getInstance().getTimeInMillis();
	}
	
	
	public static Finder<Long,Booking> find = new Finder<Long,Booking>(
			Long.class, Booking.class
			); 
	
	public String toString() {
		return "id: " + this.id + " cabin" + this.getCabin().name;
	}
	
	
	/**
	 * Method finds all bookings submitted by a user, and returns a
	 * subset of them defined by page and pageSize parameter.
	 * @param user
	 * @param page
	 * @param pageSize
	 * @return List of bookings submitted by user
	 */
	public static Page getBookingPageByUser(User user, int page, int pageSize) {
		if(user != null) {
			Page bookingPage = new Page();
			 bookingPage.orders = find.where()
			         .and(Expr.eq("user", user), Expr.ne("status", CANCELLED))
			         .orderBy("timeOfBooking asc")
			         .findPagingList(pageSize)
			         .getPage(page).getList();
			 bookingPage.totalItems = user.getNrOfBookings();
			 return bookingPage;
		}
		return new Page();
	}
	
	
	/**
	 * Returns a booking object.
	 * @param bookingId - unique id of booking
	 * @return Booking object
	 */
	public static Booking getBookingById(String bookingId) {
		return Booking.find.where().eq("id", bookingId).findUnique();
	}
	
	
	public static Booking createBooking(Long userId, Date dateFrom, Date dateTo, 
			Long cabinId,
			List<Bed> beds) {

		Booking b = new Booking();
		b.user = User.find.byId(userId);
		Cabin cabin = Cabin.find.byId(cabinId);

		if(cabin instanceof SmallCabin) {
			b.smallCabin = (SmallCabin)cabin;
		}
		else {

			for(Bed bed: beds) {
				b.addBed(bed);	
			}

		}

		b.dateTo = dateFrom;
		b.dateFrom = dateTo;
		b.save();

		return b;
	}
}


