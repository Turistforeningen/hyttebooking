package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.joda.time.DateTime;

import play.data.validation.Constraints;

@Entity
@DiscriminatorValue("LARGE_CABIN")
public class LargeCabin extends Cabin {

	@Constraints.Required
	@OneToMany
	public List<Bed> beds;
	

	public LargeCabin(String name, List<Bed> beds) {
		super(name);
		this.beds = beds;
	}
	
	/**
	 * Book a large cabin, supply number of beds and fromDate to toDate.
	 * @param numberOfBeds
	 * @param fromDate
	 * @param toDate
	 * @return null if availBeds.size() < numberOfBeds, else availBeds 
	 */
	public ArrayList<Bed> book(int numberOfBeds, DateTime fromDate, DateTime toDate) {
		
		ArrayList<Bed> availBeds = new ArrayList<Bed>(); /** Consider using auto-sorted collection **/
		for(Bed b: beds)
		{
			if(b.isAvailable(fromDate, toDate))
				availBeds.add(b);
		}
		
		if(availBeds.size() < numberOfBeds)
			return null;
		return availBeds; //TODO crop this to be size() == numberOfBeds
	}
	
	/** Calendar display for given date, given numberOfBeds **/
	public boolean isAvailable(Date date, int numberOfBeds) {
		
		int count = 0; //counts number of beds available for given date
		for(Bed b: beds) {
			if(b.isAvailable(date))
				count++;
		}
		
		if(count >= numberOfBeds)
			return true;
		else
			return false; //TODO
	}
}
