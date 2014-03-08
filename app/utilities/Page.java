package utilities;

import java.util.List;

import models.Booking;
/**
 * Helper object for json serializing
 * Includes the total size of booking, and
 * will help with pagination on frontend
 * @author Olav
 *
 */
public class Page {
	public static final int DEFAULT_PAGE_SIZE = 10;
	public static final int DEFAULT_PAGE = 0;
	
	public Integer totalItems;
	public List<Booking> orders;
	
	
}
