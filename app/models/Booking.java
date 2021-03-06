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
	public static final Integer TIMEDOUT = (int)3;
	public static final Integer CANCELLED = (int)2;
	public static final Integer PAID = (int)1;
	public static final Integer BOOKED = (int)0;

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
		return isAbleToCancel(CANCELLATION_LIMIT);
	}
	
	@JSON(include = false)
	private boolean isAbleToCancel(int days) {
		//This logic should probably be placed somewhere else?
		if(DateTime.now().plusDays(days).withTimeAtStartOfDay().isAfter(this.dateFrom.getMillis()) || this.status == CANCELLED) {
			return false;
		}
		else {
			return true;
		}
	}
	
	
	public boolean isAdminAbleToCancel() {
		return isAbleToCancel(0);
	}
	/**
	 * The date a booking is regarded as delivered, and payment from user expected.
	 * Since nets wont accept payments with delivery date more than 3 months in the future, all
	 * bookings with start time more than three months in the future is collected after 3 months.
	 * This can be changed to an authentication of payment, and a capture of payment manually.
	 * 
	 * ---What happens if card payment is registered on is out of date on time of capture? ---
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
	 * A getter which return number of beds booked in a largeCabin. Used by front-end (json serialized)
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
			         .and(Expr.eq("user", user), Expr.eq("status", PAID))
			         .orderBy("dateFrom asc")
			         .findPagingList(pageSize)
			         .getPage(page).getList();
			 bookingPage.totalItems = find.where()
			         .and(Expr.eq("user", user), Expr.eq("status", PAID))
			         .findRowCount();
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

	/**
	 * @param userId The userId that is conducting the booking, if invalid id returns null
	 * @param dateFrom Must be before dateTo
	 * @param dateTo Must be after dateFrom
	 * @param cabinId Must be of either SmallCabin or LargeCabin type
	 * @param beds The beds list that booking is for, if null then cabin should be small, otherwise returns null
	 * @return Booking if successfully created, null otherwise
	 */
	public static Booking createBooking(Long userId, DateTime dateFrom, DateTime dateTo, 
			Long cabinId,
			List<Bed> beds) {

		Booking b = new Booking();
		b.user = User.find.byId(userId);
		Cabin cabin = Cabin.find.byId(cabinId);

		if ( b.user == null )
			return null;
		if (!utilities.DateHelper.valid(dateFrom, dateTo)) //if date isn't valid
			return null;
		if( cabin instanceof SmallCabin ) {
			b.smallCabin = (SmallCabin)cabin;
		}
		else if ( cabin instanceof LargeCabin ){
			if( beds == null ) 
				return null;
			for( Bed bed: beds ) {
				b.addBed(bed);	
			}
		}

		b.dateTo = dateTo;
		b.dateFrom = dateFrom;
		b.save();

		return b;
	}
}


