package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

/**
 * Each cabin has a PriceMatrix, the administrators of each tourist agency can 
 * create their own personTypes and associated priceMatrices with them
 */
@Entity 
public class Price extends Model {
	
    @Id
    public Long id;
	
    @ManyToMany(mappedBy = "priceMatrix")
    public List<LargeCabin> largeCabins = new ArrayList<LargeCabin>();
    
    @OneToOne(mappedBy = "priceForCabin")
	public SmallCabin smallCabin;
    
    /** The name of the category, e.g. "Honnør", "Skoleungdom" **/
	@Constraints.Required
	public String guestType;
	
	/** String containing the age-range for guestType displayed. e.g. "0-12" for guestType.BARN.
	 * If age-range is irrelevant, leave string empty **/
	@Constraints.Required
	public String ageRange;
	
	/** The price for the cabin for members and non-members**/
	@Constraints.Required
	public double nonMemberPrice;
	@Constraints.Required
	public double memberPrice;
	
	public Price(String guestType, String ageRange, double nonMemberPrice, double memberPrice) {
		this.guestType = guestType;
		this.ageRange = ageRange;
		this.nonMemberPrice = nonMemberPrice;
		this.memberPrice = memberPrice;
	}
	
	public double getPrice(boolean isMember) {
		if(isMember) {
			return this.memberPrice;
		}
		else {
			return this.nonMemberPrice;
		}
	}
	
	public String getGuestType(boolean isMember) {
		if(isMember) {
			return this.guestType +", medlem";
		}
		else {
			return this.guestType;
		}
	}
	
	public static Finder<Long, Price> find = new Finder<Long, Price>(Long.class, Price.class);
	
	public static Price findPriceBelongingToCabin(Long cabinId, Long priceId) {
		Cabin cabin = Cabin.find.byId(cabinId);
		if(cabin instanceof LargeCabin) {
			System.out.println(cabinId);
			List<Price> prices = ((LargeCabin)cabin).priceMatrix;
			for(Price p: prices) {
				if(p.id.equals(priceId)) {
					return p;
				}
			}
			return null;
		}
		else if(cabin instanceof SmallCabin) {
			Price price = null;
			price = ((SmallCabin)cabin).priceForCabin;
			if(price.id.equals(priceId)) {
				return price;
			}
			else {
				return null;
			}
		}
		return null;
	}
}
