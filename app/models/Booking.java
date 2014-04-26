package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.*;
import org.joda.time.DateTime;
import com.avaje.ebean.Expr;
import flexjson.JSON;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import utilities.DateHelper;
import utilities.Page;

@Entity
public class Booking extends Model {
	//TODO maybe should be using enumerator in utilities package instead?
	public static final int CANCELLATION_LIMIT = 7; /** If booking takes place in less than or equal to seven days; cannot cancel **/
	public static Integer TIMEDOUT = new Integer(3);
	public static Integer CANCELLED = new Integer(2);
	public static Integer PAID = new Integer(1);
	public static Integer BOOKED = new Integer(0);
	
	@Id
	public Long id;
	
	@Constraints.Required
	@Formats.DateTime(pattern="yyyy-MM-dd")
	public DateTime dateFrom;

	@Constraints.Required
	@Formats.DateTime(pattern="yyyy-MM-dd")
	public DateTime dateTo;
	
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
	public SmallCabin smallCabin;
	
	@ManyToMany(mappedBy = "bookings",cascade = CascadeType.ALL)
	public List<Bed> beds = new ArrayList<Bed>();
	
	public void addBed(Bed b) {
		beds.add(b);
		//b.update(); //TODO shouldn't update run here?
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
	
	/**
	 * Getter for payment object. Json(include = false) ensures that
	 * jonFlex serializing wont serialize sensitive data.
	 * @return Payment - containing transactionId, date of payment
	 */
	@JSON(include = false)
	public Payment getPayment() {
		return this.payment;
	}
	/**
	 * Determines if booking can be cancelled or not. Can be used
	 * by both frontend (json serialized) and backend to verify a request to
	 * cancel a booking.
	 * @return boolean
	 */
	public boolean isAbleToCancel() {
		//This logic should probably be placed somewhere else?
		if(DateTime.now().plusDays(CANCELLATION_LIMIT).withTimeAtStartOfDay().isAfter(this.dateFrom.getMillis())) {
			return false;
		}
		else {
			return true;
		}
	}
	
	/**
	 * The date a booking is regarded as delivered, and payment from user expected.
	 * Since nets wont accept payments with deliverydate more than 3 months in the future, all
	 * bookings with start time more than three months in the future is collected after 3 months.
	 * This can be changed to an authentication of payment, and a capture of payment manually.
	 * 
	 * ---What happpens if card payment is registered on is out of date on time of capture? ---
	 * @return date of delivery
	 */
	@JSON(include = false)
	public String getDeliveryDate() {
		DateTime orderTime = dateFrom;
		DateTime now = DateTime.now();
		if(now.plusMonths(3).isAfter(orderTime)) {
			return DateHelper.dtToYYYYMMDDString(dateFrom);
		}
		else {
			return DateHelper.dtToYYYYMMDDString(now.plusMonths(3));
		}
		
	}
	/**
	 * A getter which return number of beds booked in a largeCabin. Used by frontend (json serialized)
	 * @return String - number of beds in order or cabin 
	 */
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
	public static Page<Booking> getBookingPageByUser(User user, int page, int pageSize) {
		System.out.println(page + " " + pageSize);
		if(user != null) {
			Page<Booking> bookingPage = new Page<Booking>();
			 bookingPage.data = find.where()
			         .and(Expr.eq("user", user), Expr.le("status", PAID))
			         .orderBy("dateFrom asc")
			         .findPagingList(pageSize)
			         .getPage(page).getList();
			 bookingPage.totalItems = user.getNrOfBookings();
			 return bookingPage;
		}
		return new Page<Booking>();
	}
	
	
	/**
	 * Returns a booking object.
	 * @param bookingId - unique id of booking
	 * @return Booking object
	 */
	public static Booking getBookingById(String bookingId) {
		return Booking.find.where().eq("id", bookingId).findUnique();
	}
	
	public Long getDateFrom() {
		return this.dateFrom.getMillis();
	}
	
	public Long getDateTo() {
		return this.dateTo.getMillis();
	}
	
	public static Booking createBooking(Long userId, DateTime dateFrom, DateTime dateTo, 
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

		b.dateTo = dateTo;
		b.dateFrom = dateFrom;
		b.save();

		return b;
	}
}


