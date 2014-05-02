package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.joda.time.DateTime;

import flexjson.JSON;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class Payment extends Model {
	
	public static Finder<Long, Payment> find = new Finder<Long, Payment>(Long.class, Payment.class);
	
	@Id 
	public Long id;
	
	@Constraints.Required
	public Double amount;
	
	@Constraints.Required
	public DateTime date;
	
	public String transactionId;
	
	/** User who authorised transaction **/
	@Constraints.Required
	public User user;	
	
	@Constraints.Required
	@OneToMany(mappedBy="payment") //you were here a
	public List<Guest> guests = new ArrayList<Guest>();
	
	@OneToOne(mappedBy = "payment")
	public Booking booking;
	
	//Nets accepted amount string
	public String getNetsAmount() {
		return (int)Math.floor(amount*100)+"";
	}

	public void setTransactionId(String trans) {
		transactionId = trans;
		this.update();
	}
	
	@JSON(include = false)
	public String getTransactionId() {
		return this.transactionId;
	}
	
	public static Payment createPaymentForBooking(User user, Booking b, double amount, List<Guest> g) {
		if(user == null || b == null || amount < 0.0) {
			return null;
		}
		if(!b.user.id.equals(user.id)) {
			return null;
		}
		Payment p = new Payment();
		p.date = DateTime.now();
		p.user = user;
		p.amount = amount;
		p.booking = b;
		p.guests = g;
		p.save();
		b.payment = p;
		b.update();
		for(Guest guest : g) {
			guest.payment = p;
			guest.update();
		}
		
		return p;
	}
}
