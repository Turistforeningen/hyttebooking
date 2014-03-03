package models;

import java.util.Date;

import javax.persistence.*;

import play.db.ebean.Model;

/**
 * Abstract superclass for SmallCabin and LargeCabin. This is taken straight from JPA inheritance
 * pages (http://en.wikibooks.org/wiki/Java_Persistence/Inheritance) and seems to be most popular
 * solution.
 * @author Jama
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Cabin extends Model {

	@Id
	public Long id;
	
	public String name;
	
	public Cabin(String name) {
	this.name = name;	
	}
	
	public static Finder<Long, Cabin> find = new Finder<Long, Cabin>(Long.class, Cabin.class);
	
	/** Checks availability of cabin for given date. Use for SmallCabin **/
	public boolean isAvailable(Date date) {
		return false; //TODO unimplemented method
	}
	
	/** Checks availability of cabin for given date, and number of beds specified. Used with LargeCabin **/
	public boolean isAvailable(Date date, int numberOfBeds) {
		return false; //TODO unimplemented method
	}
}
