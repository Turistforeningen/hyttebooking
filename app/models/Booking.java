package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

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
	
	public void removeAllBeds() {
		beds.clear();
		
	}
	
	/**
	 * 
	 * @return Cabin
	 */
	public Cabin getCabin() {
		if(beds.size() != 0) {
			System.out.println(beds.size());
			System.out.println(beds.get(0).largeCabin);
			return beds.get(0).largeCabin;
		}
		else {
			return smallCabin;
		}
	}
	
	/** TEST **/
	public Booking(Long userId,
			Date dayOfBookingStart,
			Date dayOfBookingEnd,
			Long cabinId,
			List<Bed> beds) {
		this.timeOfBooking = Calendar.getInstance().getTimeInMillis();
		
		this.user = User.find.byId(userId);
		Cabin cabin = Cabin.find.byId(cabinId);
		
		if(cabin instanceof SmallCabin) {
			this.smallCabin = (SmallCabin)cabin;
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

}


