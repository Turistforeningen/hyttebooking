package utilities;

import org.joda.time.DateTime;

import flexjson.transformer.AbstractTransformer;

/**
 * Transformer that convert a dateTime object into millis when serializing
 * @author Olav
 *
 */
public class DateTimeTransformer extends AbstractTransformer {


	public void transform(Object object) {
		DateTime d = (DateTime)object;
		getContext().writeQuoted(d.getMillis()+"");
	}



}
