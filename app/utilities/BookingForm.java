package utilities;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;


import controllers.SecurityController;
import flexjson.JSONDeserializer;
import models.Bed;
import models.Booking;
import models.Cabin;
import models.LargeCabin;
import models.Payment;

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
	public int beds;
	public List<PriceForm> guests;
	
	/**
	 * FlexJson needs an constructor even if its empty
	 */
	public BookingForm() {
		
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
			List<Bed> bedsO = null;
			if (cabin instanceof LargeCabin) {
				bedsO = ((LargeCabin) cabin).book(beds, startDt, endDt);
				if(bedsO == null) {
					addError("No beds available for time period selected");
					return null;
				}
			}
			
			Booking booking = Booking.createBooking(
					SecurityController.getUser().id, 
					startDt.toDate(),
					endDt.toDate(),
					cabin.id,
					bedsO);
			
			double amount = PriceHelper.calculateAmount(guests, Days.daysBetween(startDt, endDt).getDays());
			Payment.createPaymentForBooking(SecurityController.getUser(), booking, amount);
			addSuccess("message", "booking saved");
			
			
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
		if(cabinId == null) {
			addError("CabinId parameter not set");
			return false;
		}
		
		if(guests == null) {
			addError("Guests array missing");
			return false;
		}
		
		int nrOfGuests = 0;
		for(PriceForm p: guests) {
			nrOfGuests += p.nr;
		}
		if(nrOfGuests<=0) {
			addError("You have to book for at least person");
			return false;
		}
		//check here if booking only contains children or babies, which should not be possible
		Cabin cabin = Cabin.find.byId(cabinId);
		if(cabin == null) {
			addError("Can't book at this cabin");
			return false;
		}
		
		if(dateFrom == null || dateTo == null) {
			addError("DateTo or dateFrom are missing");
			return false;
		}
		DateTime startDt = utilities.DateHelper.toDt(this.dateFrom);
		if(startDt == null) {
			addError("FromDate parameter is invalid");
			return false;
		}
		
		DateTime endDt = utilities.DateHelper.toDt(this.dateTo);
		if(endDt == null) {
			addError("ToDate parameter is invalid");
			return false;
		}
		
		if(startDt.isBeforeNow() || endDt.isBeforeNow() || !startDt.isBefore(endDt))  {
			System.out.println(startDt +" "+ endDt);
			addError("The range selected is not valid");
			return false;	
		}
		
		
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
		try {
			JSONDeserializer<BookingForm> deserializer = new JSONDeserializer<BookingForm>()
					.use("guests.values", PriceForm.class);
			bookingData = deserializer.deserialize(jsonBooking , BookingForm.class);
		} catch (Exception e) {
			e.printStackTrace();
			//add more detailed data. Possibly what variable it cant find.
			bookingData = new BookingForm();
			bookingData.addError("Could not parse data");
			bookingData.validationError = true;
			
		}
		return bookingData;
	}
}
