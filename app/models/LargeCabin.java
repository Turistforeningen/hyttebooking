package models;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import play.data.validation.Constraints;

@Entity
public class LargeCabin extends Cabin {

	@Constraints.Required
	@OneToMany
	public List<Bed> beds;

	@Override
	public boolean isAvailable(Date date, int numberOfBeds) {
		// TODO Auto-generated method stub
		return false;
	}
}
