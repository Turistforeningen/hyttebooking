package models;

import javax.persistence.*;

import play.db.ebean.Model;

/**
 * Abstract superclass for SmallCabin and LargeCabin. This is taken straight from JPA inheritance
 * pages (http://en.wikibooks.org/wiki/Java_Persistence/Inheritance) and seems to be most popular
 * solution.
 * @author Jama
 */
@Entity
@Inheritance
@DiscriminatorColumn(name="CABIN_TYPE")
@Table(name="CABIN")
public abstract class Cabin extends Model {

	private static final long serialVersionUID = 1L;
	
	@Id
	public Long id;
	
	public static Finder<Long, Cabin> find = new Finder<Long, Cabin>(Long.class, Cabin.class);
}
