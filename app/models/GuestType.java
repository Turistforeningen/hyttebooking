package models;

import javax.persistence.Column;
import javax.persistence.Id;

import play.data.validation.Constraints;
import play.db.ebean.Model;

/**
 * Right now this is connected to guest, and the usage should be to multiply price
 * here with cabin.price
 * 
 * E.g. GuestType.price * Cabin.price = 0.8 * 350 = 280,-
 * -------
 * 
 * The other implementation I can see is GuestType being connected to Cabin, then Cabin.price
 * is removed and replaced with Cabin.GuestType.price and is multiplied with number of guests
 * E.g. Cabin.GuestType.price * Guests = 280 * 1 = 280,-
 * @author Jama
 */
@Deprecated /** Use LargeCabin.priceMatrix **/
public class GuestType extends Model {

	@Id
	public Long id;
	
	@Constraints.Required
	@Constraints.MinLength(2)
    @Constraints.MaxLength(32)
	@Column(length = 256, nullable = false)
	public String typeName; /** The name of the type e.g. "Child", "12-14", "Barn" **/
	
	@Constraints.Required
	public int memberShip; /** The type of membership guest has (Consider 0 = not member, 1 = member) **/
	
	@Constraints.Required
	public double price; /** The price for this type **/
	
}
