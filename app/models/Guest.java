package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import play.db.ebean.Model;

@Entity
public class Guest extends Model {

	@Id
	public Long id;
	
	@OneToOne
	public Booking booking;

	/** TODO REMOVE TEST **/
	public Guest(long id) {
		this.id = id;
	}
	/** END TEST **/
}