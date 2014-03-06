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
	@OneToMany(mappedBy="largeCabin", cascade = CascadeType.ALL, orphanRemoval=true)
	public List<Bed> beds;
	
	/**
	 * 
	 * @param name
	 * @param nrOfBeds
	 */
	public LargeCabin(String name, int nrOfBeds) {
		super(name);
		for(int i= 0; i<nrOfBeds; i++) {
			addBed();
		}
	}
	
	/**
	 * Admin method
	 */
	public void addBed() {
		if (beds == null) {
			beds = new ArrayList<Bed>();
		}
		Bed newBed = new Bed();
		newBed.largeCabin = this;
		beds.add(newBed);
		newBed.save();
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
	
	/**
	 * Admin method
	 */
	public void removeBed() {
		if (beds.size() <= 1) {
			return;
		}
		//add support for removing a spesific bed
		beds.remove(0);
	}
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
