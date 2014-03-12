package utilities;

import java.text.SimpleDateFormat;

import org.joda.time.DateTime;
import org.joda.time.Days;

public class DateHelper {

	/** Helper method for dateTime object from string "dd-MM-YYYY" **/
	public static DateTime toDt(String date) {
		
		String[] d = date.split("-");
		if(d.length < 3) return null;
		DateTime dt = new DateTime(Integer.parseInt(d[0]), //int year
				Integer.parseInt(d[1]), //int month
				Integer.parseInt(d[2]), //int day
				0, 						//int hour
				0						//int minute
				);
		return dt;
	}
	
	/**
	 * Checks if two date ranges overlap. If any of the dates are the same it is regarded as overlap
	 * If there is a need to change so that toDate and fromDate2 (or vice versa) can overlap, then
	 * remove the .equals OR lines at the end
	 * @return true if dates overlap
	 */
	public static boolean isOverlap(DateTime fromDate, DateTime toDate,
			DateTime fromDate2, DateTime toDate2) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		if((fromDate.isBefore(toDate2) && fromDate2.isBefore(toDate) ||
				((fmt.format(fromDate.toDate()).equals(fmt.format(fromDate2.toDate())) && 
						(fmt.format(fromDate.toDate()).equals(fmt.format(toDate2.toDate())))) || 
								fmt.format(fromDate2.toDate()).equals(fmt.format(toDate.toDate()))))) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if a date is within a range
	 * @param point The point between the two ranges
	 * @param fromDate 
	 * @param toDate
	 * @return true if point is within range fromDate, toDate
	 */
	public static boolean withinDate(DateTime point, DateTime fromDate, DateTime toDate) {
		return ( (point.isAfter(fromDate) && point.isBefore(toDate)) || point.equals(fromDate) || point.equals(toDate));
	}

	/**
	 * Assume that startDate is 0, find difference in days for date a and b
	 */
	public static int[] getIndex(DateTime startDate, DateTime a,
			DateTime b) {
		int[] ret = new int[2];
		ret[0] = Days.daysBetween(startDate, a).getDays();
		ret[1] = Days.daysBetween(startDate, b).getDays();
		return ret;
	}


}
