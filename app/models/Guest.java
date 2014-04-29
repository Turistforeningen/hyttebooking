package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import flexjson.JSON;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class Guest extends Model {

	@Id
	public Long id;
	
	@ManyToOne
	public Payment payment;
	/** Boolean for if guest is member or not **/
	
	
	@OneToOne
	public Price priceCategory;
	
	public Boolean isMember;
	
	public Integer nr;
	
	public double getPrice() {
		return priceCategory.getPrice(isMember);
	}
	
	public String getGuestType() {
		return priceCategory.getGuestType(isMember);
	}
	
	public String getAgeRange() {
		return priceCategory.ageRange;
	}
	public Guest(Price p, boolean isMember, int numberInCategory) {
		this.priceCategory =p;
		this.isMember = isMember;
		this.nr = numberInCategory;
	}
	/** END TEST **/
	public static Finder<Long, Guest> find = new Finder<Long, Guest>(Long.class, Guest.class);
}