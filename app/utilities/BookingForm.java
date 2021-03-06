package utilities;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;









import com.fasterxml.jackson.databind.JsonNode;

import play.i18n.Messages;
import controllers.SecurityController;
import flexjson.JSON;
import flexjson.JSONDeserializer;
import models.Bed;
import models.Booking;
import models.Cabin;
import models.Guest;
import models.LargeCabin;
import models.Payment;
import models.Price;
import models.SmallCabin;

/**
 * Subclass of AbstractForm. Binds and validate json data used to create
 * a Booking in the system.
 * @author Olav
 *
 */
public class BookingForm extends AbstractForm<Booking> {
	public Long cabinId;
	public String dateFrom;
	public String dateTo;
	public List<PriceForm> guests;
	private int nrOfGuests = 0;
	public boolean termsAndConditions;
	private double amount;
	private List<Guest> guestList;
	/**
	 * FlexJson needs an constructor even if its empty
	 */
	public BookingForm() {
		this.guestList = new ArrayList<Guest>();
	}

	
	/**
	 * CreateModel creates an Booking and saves it to the database.
	 * If the form is not valid, it returns null.
	 * It check that there is available beds if the cabin booked at is
	 * a largeCabin, and saves payment information like the amount 
	 * to be paid.
	 */
	@Override
	public Booking createModel() {
		if(isValid()) {
			//save these in validate? Or messy
			DateTime startDt = utilities.DateHelper.toDt(this.dateFrom);
			DateTime endDt = utilities.DateHelper.toDt(this.dateTo);
			Cabin cabin = Cabin.find.byId(cabinId);
			List<Bed> beds = null;
			
			if (cabin instanceof LargeCabin) {
				beds = ((LargeCabin) cabin).book(this.nrOfGuests, startDt, endDt);
				if(beds == null) {
					addError(Messages.get("booking.bedsNotAvailable"));
					return null;
				}
			}
			else if(cabin instanceof SmallCabin) {
				boolean bookable = ((SmallCabin) cabin).isAvailable(startDt, endDt);
				if(!bookable) {
					addError(Messages.get("booking.cabinNotAvailable"));
					return null;
				}
			}
			System.out.println(SecurityController.getUser().id);
			Booking booking = Booking.createBooking(
					SecurityController.getUser().id, 
					startDt,
					endDt,
					cabin.id,
					beds);
			
			double amount = this.amount;
			Payment.createPaymentForBooking(SecurityController.getUser(), booking, amount, this.guestList);
			addSuccess("message", Messages.get("booking.successful"));
			
			
			return booking;
		}
		
		return null;
	}

	/**
	 * Validate is responsible for checking that no data is missing from the json request,
	 * and that data is correctly inputted. If there is any missing or wrong
	 * parameters, then the form is deemed invalid.
	 */
	@Override
	public boolean validate() {
		
		if(termsAndConditions == false) {
			addError(Messages.get("booking.termsAndConditions"));
			return false;
		}
		//A user should be logged in before booking.
		if(cabinId == null) {
			addError(Messages.get("booking.misssingCabinId"));
			return false;
		}
		
		//check here if booking only contains children or babies, which should not be possible
		Cabin cabin = Cabin.find.byId(cabinId);
		if(cabin == null) {
			addError(Messages.get("booking.missingCabin"));
			return false;
		}
		
		if(dateFrom == null || dateTo == null) {
			addError(Messages.get("booking.missingDates"));
			return false;
		}
		DateTime startDt = utilities.DateHelper.toDt(this.dateFrom);
		if(startDt == null) {
			addError(Messages.get("booking.invalidFromDate"));
			return false;
		}
		
		DateTime endDt = utilities.DateHelper.toDt(this.dateTo);
		if(endDt == null) {
			addError(Messages.get("booking.invalidToDate"));
			return false;
		}
		
		if(startDt.	isBeforeNow() || endDt.isBeforeNow() || !startDt.isBefore(endDt))  {
			addError(Messages.get("booking.invalidDateRange"));
			return false;	
		}
		if(guests == null) {
			addError(Messages.get("booking.missingGuestArray"));
			return false;
		}
		
		boolean proccessPrices = processGuestList(this.guests, this.cabinId, Days.daysBetween(startDt, endDt).getDays());
		if(proccessPrices == false) {
			
			return false;
		}
		
		if(this.nrOfGuests<=0) {
			addError(Messages.get("booking.atLeastOnePerson"));
			return false;
		}
			
		return true;
	}
	
	private boolean processGuestList(List<PriceForm> guestList, Long cabinId, int days) {
		double amount = 0;
		int beds = 0;
		boolean onlyMinors = true;
		for(PriceForm guestType : guestList) {
			if(guestType.nr >0) {
				Price price= Price.findPriceBelongingToCabin(cabinId, guestType.id);
				
				if(price == null) {
					addError(Messages.get("booking.guestArrayInvalid"));
					return false;
				}
				if(price.isMinor == false) {
					onlyMinors = false;
				}
				beds += guestType.nr;
				amount += days*guestType.nr*price.getPrice(guestType.isMember);
				Guest g = new Guest(price, guestType.isMember, guestType.nr);
				this.guestList.add(g);
			}			
		}
		
		if(onlyMinors) {
			addError(Messages.get("booking.guestOnlyMinor"));
			return false;
		}
		
		for(Guest guest : this.guestList) {
			guest.save();
		}
		this.nrOfGuests = beds;
		this.amount = amount;
		return true;
	}
	/**
	 * jsonFlex is used to deserialize/unmarshall json String into a form containing
	 * java classes like String, int, List<T>. If the deserializer cant complete,
	 * it is counted as an error.  
	 * 
	 * @param jsonBooking
	 * @return BookingForm used to validate and bind the data.
	 */
	public static BookingForm deserializeJson(String jsonBooking) {
		BookingForm bookingData = null;
		//System.out.println(jsonBooking);
		try {
			JSONDeserializer<BookingForm> deserializer = new JSONDeserializer<BookingForm>()
					.use("guests.values", PriceForm.class);
			bookingData = deserializer.deserialize(jsonBooking , BookingForm.class);
		} catch (Exception e) {
			e.printStackTrace();
			//add more detailed data. Possibly what variable it cant find.
			System.out.println(e.getMessage());
			bookingData = new BookingForm();
			bookingData.addError(Messages.get("json.couldNotParseData"));
			bookingData.validationError = true;
			
		}
		return bookingData;
	}
}
