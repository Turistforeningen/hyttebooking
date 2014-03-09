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
	
	public static int pageHelper(String page) {
		try {
			return Integer.parseInt(page);
		} catch (Exception e) {
			return DEFAULT_PAGE;
		}
	}
	
	public static int pageSizeHelper(String pageSize) {
		try {
			return Integer.parseInt(pageSize);
		} catch (Exception e) {
			return DEFAULT_PAGE_SIZE;
		}
	}
}
