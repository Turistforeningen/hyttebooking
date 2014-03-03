package models;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

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

	
	public boolean isAvailable(Date date, int numberOfBeds) {
		// TODO Auto-generated method stub
		return false;
	}
}
