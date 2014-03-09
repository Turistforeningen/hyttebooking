package models;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import play.db.ebean.Model;
import utilities.Page;
import views.html.play20.book;

/**
 * Abstract superclass for SmallCabin and LargeCabin. This is taken straight from JPA inheritance
 * pages (http://en.wikibooks.org/wiki/Java_Persistence/Inheritance) and seems to be most popular
 * solution.
 * @author Jama
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class Cabin extends Model {

	@Id
	public Long id;
	
	public String name;
	
	public Cabin(String name) {
	this.name = name;	
	}
	
	public static Finder<Long, Cabin> find = new Finder<Long, Cabin>(Long.class, Cabin.class);
	
	/**
	 * Returns a page of bookings for a given cabin. What bookings and the size of the list are
	 * decided by page and pageSize arguments.
	 * 
	 * @param cabinId
	 * @param page 
	 * @param pageSize
	 * @return
	 */
	public static Page findAllBookingsForCabin(Long cabinId, int page, int pageSize) {
		Cabin cabin = Cabin.find.byId(cabinId);
		Page bookingPage = new Page();
		if(cabin instanceof SmallCabin) {
			
			bookingPage.orders = Booking.find.where()
					.eq("smallCabin", cabin)
					.findPagingList(pageSize)
			        .getPage(page).getList();
			bookingPage.totalItems = new Integer(((SmallCabin)cabin).bookings.size());
			return bookingPage;
			
		}
		else if(cabin instanceof LargeCabin) {
			return null;
		}
		return null;
	}
}
