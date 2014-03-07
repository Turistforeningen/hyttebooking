package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class Guest extends Model {

	@Id
	public Long id;
	
	/** Boolean for if guest is member or not **/
	@Constraints.Required
	public boolean isMember;
	
	/** The type of guest **/
	@Constraints.Required
	public GuestType guestType;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "guest")
	@JsonIgnore
	public List<Booking> booking =new ArrayList<Booking>();

	/** TODO REMOVE TEST **/
	public Guest(long id) {
		this.id = id;
	}
	/** END TEST **/
	public static Finder<Long, Guest> find = new Finder<Long, Guest>(Long.class, Guest.class);
}