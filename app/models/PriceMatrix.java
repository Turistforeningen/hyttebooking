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
public class PriceMatrix extends Model {
	
    @Id
    public Long id;
	
    /** The type of person, TODO maybe enumerator, maybe too cryptic **/
	@Constraints.Required
	int personType;
	
	/** String containing the name displayed for this personType, TODO reconsider relationship betwixt previous field**/
	@Constraints.Required
	String displayedName;
	
	/** The price for the cabin for this personType **/
	@Constraints.Required
	double price;
}
