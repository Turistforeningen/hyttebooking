package controllers;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import utilities.DateHelper;
import flexjson.transformer.AbstractTransformer;

public class DateTimeTransfomer2 extends AbstractTransformer {

	public void transform(Object object) {
		DateTime d = (DateTime)object;
		getContext().writeQuoted(dtToStringHyphenated(d));
	}
	
	public static String dtToStringHyphenated(DateTime time) {
		DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
		return dtf.print(time);
	}
}