package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import javax.persistence.*;
import org.joda.time.DateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import flexjson.JSON;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class User extends Model {

	/** assosier med sherpa id i DNT **/
    @Id
    public Long id;
    
    public String authToken;
    
    @Column(length = 256, nullable = false)
    @Constraints.MaxLength(256)
    @Constraints.Required
    @Constraints.Email
    private String emailAddress;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress.toLowerCase();
    }
    
    @JSON(include = false)
    public String getAuthToken() {
    	return this.authToken;
    }

    @Column(length = 256, nullable = false)
    @Constraints.Required
    @Constraints.MinLength(2)
    @Constraints.MaxLength(256)
    public String fullName;

    public Boolean admin= false;
    
    @Column(nullable = false)
    public DateTime creationDate;

    public String createToken() {
    	System.out.println("WRITING A TOKEN");
        authToken = UUID.randomUUID().toString();
        try {
        	save();
		} catch (OptimisticLockException e) {
			e.printStackTrace();
			
			save();
		}
        
        return authToken;
    }

    public void deleteAuthToken() {
        authToken = null;
       	save();
    }
    
	@OneToMany
	@JsonIgnore
	public List<Booking> bookings = new ArrayList<Booking>();
    
    public User() {
        this.creationDate = new DateTime();
    }

    @Deprecated
    /**
     * Use constructor with id in field, as any user has to log in via DNT connect first
     * id is 1:1 associated with sherpa_id gotten
     */
    public User(String emailAddress, String password, String fullName) {
        setEmailAddress(emailAddress);
        //setPassword(password);
        this.fullName = fullName;
        this.creationDate = new DateTime();
    }
    
    public User(long sherpaId, String emailAddress, String fullName) {
        setEmailAddress(emailAddress);
        this.fullName = fullName;
        this.creationDate = new DateTime();
        this.id = sherpaId;
    }
    
    public int getNrOfBookings() {
    	return this.bookings.size();
    }
    
    public static byte[] getSha512(String value) {
        try {
            return MessageDigest.getInstance("SHA-512").digest(value.getBytes("UTF-8"));
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);
    
    public static User findByAuthToken(String authToken) {
        if (authToken == null) {
            return null;
        }

        try  {
            return find.where().eq("authToken", authToken).findUnique();
        }
        catch (Exception e) {
            return null;
        }
    }

	public static User findBySherpaId(long id) {
		return find.byId(id);
	}
    
}
