package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Constraint;

import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class Bed extends Model {

	@Id
	public Long id;
	
	@Constraints.Required
	@ManyToOne
	public LargeCabin largeCabin;
	
	@OneToMany
	public Booking booking;
}
