package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class Guest extends Model {

	@Id
	public Long id;
	
	@ManyToOne
	public Booking booking;
	/** Boolean for if guest is member or not **/
	@Constraints.Required
	public boolean isMember;
	
	/** TODO REMOVE TEST **/
	public Guest(long id) {
		this.id = id;
	}
	/** END TEST **/
	public static Finder<Long, Guest> find = new Finder<Long, Guest>(Long.class, Guest.class);
}