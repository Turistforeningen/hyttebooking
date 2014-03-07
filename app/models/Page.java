package models;

import java.util.List;
/**
 * Helper object for json serializing
 * Includes the total size of booking, and
 * will help with pagination on frontend
 * @author Olav
 *
 */
public class Page {
	public Integer totalItems;
	public List<Booking> orders;
	
	
}
