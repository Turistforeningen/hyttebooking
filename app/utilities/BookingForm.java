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

public class BookingForm extends AbstractForm<Booking> {
	public Long cabinId;
	public String dateFrom;
	public String dateTo;
	public int beds;
	public List<PriceForm> guests;
	
	public BookingForm() {
		
	}

	
	@Override
	public Booking createModel() {
		if(validate()) {
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
			addSuccess("id", booking.id +"");
			
			return booking;
		}
		
		return null;
	}

	
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
			addError("The range selected is not valid");
			return false;	
		}
		
		
		return true;
	}
	
	
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
