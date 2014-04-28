package models;

import java.util.ArrayList;
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
	public List<Bed> book(int numberOfBeds, DateTime fromDate, DateTime toDate) {
		if(numberOfBeds < 0 || !utilities.DateHelper.valid(fromDate, toDate))
			return null;
		
		ArrayList<Bed> availBeds = new ArrayList<Bed>(); /** Consider using auto-sorted collection **/
		for(Bed b: beds)
		{
			if(b.isAvailable(fromDate, toDate))
				availBeds.add(b);
		}
		
		if(availBeds.size() < numberOfBeds)
			return null;
		return availBeds.subList(0, numberOfBeds);
	}
	
	/**
	 * Admin method
	 */
	public void removeBed() {
		if (beds.size() <= 1) {
			return;
		}
		//add support for removing a specific bed
		Bed b = beds.remove(0);
		b.delete();
	}
	
	@Override
	public String getcabinType() {
		return "large";
	}

	@Override
	public String getNrOfBeds() {
		return this.beds.size() +"";
	}

	@Override
	public int getNrActiveBookings() {
		return Booking.find
				.where()
				.eq("beds.largeCabin", this)
				.gt("dateFrom", DateTime.now())
				.lt("status", Booking.CANCELLED)
				.findRowCount();
	}
	
	/**
	 * 
	 * @param guestType CANNOT be null or empty
	 * @param ageRange Can be null, but cannot be empty
	 * @param nonMemberPrice Cannot be negative
	 * @param memberPrice Cannot be negative
	 */
	public void addPrice(String guestType, String ageRange, double nonMemberPrice, double memberPrice) {
		if(nonMemberPrice < 0 || memberPrice < 0 || guestType == null || guestType.length() == 0)
			return;
		if(ageRange != null)
			if(ageRange.length() == 0)
				return;
		
		Price price = new Price(guestType, ageRange, nonMemberPrice, memberPrice);
		price.save();
		
		priceMatrix.add(price);
		
	}

	@Override
	public String getCabinUrl() {
		return this.id + "?type=large&beds=" + this.getNrOfBeds();
	}
}
