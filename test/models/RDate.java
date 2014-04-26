package models;

import org.joda.time.DateTime;

/**
 * We want to be able to run tests and verify correctness at any time, but bookings
 * are time-sensitive, so we can't neccessarily run tests that have "2014" as date
 * in the year 2015 for example.
 * So we set all date examples in this static object and can change this object as needed
 * when the years pass.
 * 
 * All dateTime objects to be used in tests should be relative to this, hence RDate relative date.
 */
public class RDate {

	final static String NEXT_YEAR = "2015"; /** Change this to always be set as next year **/
	final static DateTime fDt = new DateTime(NEXT_YEAR+"-01-01"); /** Always set to jan 1 of the next year **/
}
