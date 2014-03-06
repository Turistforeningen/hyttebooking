package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

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
		// TODO Auto-generated method stub
		return false;
	}
}
