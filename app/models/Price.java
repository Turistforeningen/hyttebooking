package models;

import javax.persistence.Id;

import play.data.validation.Constraints;
import play.db.ebean.Model;

/**
 * Each cabin has a PriceMatrix, the administrators of each tourist agency can 
 * create their own personTypes and associated priceMatrices with them
 * @author Jama
 *
 */
public class Price extends Model {
	
    @Id
    public Long id;
	
    /** The name of the category, e.g. "Honnør", "Skoleungdom" **/
	@Constraints.Required
	public String guestType;
	
	/** String containing the age-range for guestType displayed. e.g. "0-12" for guestType.BARN.
	 * If age-range is irrelevant, leave string empty **/
	@Constraints.Required
	public String ageRange;
	
	/** The price for the cabin for this guestType **/
	@Constraints.Required
	public double nonMemberPrice;
	
	@Constraints.Required
	public double memberPrice;
	
	public Price(Long id, String guestType, String ageRange, double nonMemberPrice, double memberPrice) {
		this.id = id;
		this.guestType = guestType;
		this.ageRange = ageRange;
		this.nonMemberPrice = nonMemberPrice;
		this.memberPrice = memberPrice;
	}
}