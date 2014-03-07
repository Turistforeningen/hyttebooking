package models;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class Payment extends Model {

	@Id 
	public Long id;
	
	@Constraints.Required
	public double amount;
	
	@Constraints.Required
	public Date date;
	
	/** User who authorised transaction **/
	@Constraints.Required
	public User user;	
}
