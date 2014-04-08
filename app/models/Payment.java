package models;

import java.sql.Date;
import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.joda.time.DateTime;

import flexjson.JSON;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class Payment extends Model {
	
	public static Finder<Long, Payment> find = new Finder<Long, Payment>(Long.class, Payment.class);
	
	@Id 
	public Long id;
	
	
	@Constraints.Required
	public double amount;
	
	@Constraints.Required
	public Date date;
	
	public String transactionId;
	
	/** User who authorised transaction **/
	@Constraints.Required
	public User user;	
	
	public Booking booking;
	
	//Nets accepted amount string
	public String getAmount() {
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
	
	public static void createPaymentForBooking(User user, Booking b, double amount) {
		Payment p = new Payment();
		p.date = new Date(DateTime.now().getMillis()); //weird way. Should be yodatime
		p.user = user;
		p.amount = amount;
		p.booking = b;
		p.save();
		b.payment = p;
		b.update();
	}
	
	
}
