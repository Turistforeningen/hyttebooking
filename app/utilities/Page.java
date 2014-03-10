package utilities;

import java.util.List;

/**
 * Helper object for json serializing
 * Includes the total size of booking, and
 * will help with pagination on frontend
 * @author Olav
 * @param <T>
 *
 */
public class Page<T> {
	public static final int DEFAULT_PAGE_SIZE = 10;
	public static final int DEFAULT_PAGE = 0;
	
	public Integer totalItems;
	public List<T> data;
	
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
