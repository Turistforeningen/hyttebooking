package models;

import org.joda.time.DateTime;

/**
 * We want to be able to run tests and verify correctness at any time, but bookings
 * are time-sensitive, so we can't neccessarily run tests that have "2014" as date
 * in the year 2015 for example.
 * So we set all date examples relative to this static object
 * 
 */
public class RDate {

	final static DateTime fDt = new DateTime(DateTime.now().getYear()+1+"-01-01").withTimeAtStartOfDay(); /** Always set to jan 1 of the next year **/
}
