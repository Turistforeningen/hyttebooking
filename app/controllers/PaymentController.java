package controllers;



	



import org.w3c.dom.Document;



import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Booking;
import models.Payment;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.SimpleResult;
import static play.libs.F.Function;
import static play.libs.F.Promise;
import play.libs.F;
import play.libs.Json;
import play.libs.WS;
import play.libs.XPath;

public class PaymentController extends Controller {
	private static final String SECRET_MERCHANT = play.Play.application().configuration().getString("application.merchantKey");
	private static final String NETS_REGISTER = "https://epayment-test.bbs.no/Netaxept/Register.aspx";
	private static final String NETS_PROCESS = "https://epayment-test.bbs.no/Netaxept/Process.aspx";
	private static final String MERCHANT_ID = play.Play.application().configuration().getString("application.merchantId");
	private static final String REDIRECT_URL = "https://epayment-test.bbs.no/Terminal/default.aspx";
	private static final String SERVER_URL = play.Play.application().configuration().getString("application.address");
	
	
	/**
	 * The register controller method are used for starting a payment using Netaxept.
	 * The purpose of the register call is to send all the data needed to complete a transaction to Netaxept servers.
	 * A call is made to Netaxept that registers a transaction and a transaction id are returned if contact is established.
	 * This transaction id is stored in the database, and a redirect url to a Netaxept payment site is returned to the user.
	 * 
	 * A deliveryDate date string are sent to nets that tells when to capture the amount payed.
	 * 
	 * http://www.betalingsterminal.no/Netthandel-forside/Teknisk-veiledning/API/Register/
	 * 
	 * @param bookingId - id of booking 
	 * @return Response - contains redirect url for user payment.
	 */
	public static Promise<Result> registerPayment(Long bookingId) {
		
		final Booking b = Booking.getBookingById(bookingId+ "");
		if(b == null || b.status == Booking.BOOKED) {
			return Promise.pure((Result) notFound("notfound"));
		}
		System.out.println(b.getDeliveryDate());
		/*if(b.user != SecurityController.getUser()) {
		return Promise.pure((Result) notFound("This is not your booking"));
		}*/
		
		final Promise<Result> resultPromise = WS.url(NETS_REGISTER)
				.setQueryParameter("merchantId", MERCHANT_ID)
				.setQueryParameter("token", SECRET_MERCHANT)
				.setQueryParameter("orderNumber", b.id+"")
				.setQueryParameter("amount", b.payment.getAmount())
				.setQueryParameter("CurrencyCode", "NOK")
				.setQueryParameter("redirectUrl", SERVER_URL + "#/booking/" + b.getCabin().getCabinUrl())
				.setQueryParameter("deliveryDate", b.getDeliveryDate())
				.get().map(
				new Function<WS.Response, Result>() {
					public Result apply(WS.Response response) {
						Document dom = response.asXml();
						if(dom == null) {
							System.out.println("lol");
							return badRequest("Cant get proper response from nets");
						}
						String excpetion = XPath.selectText("//Exception", dom);
						if(excpetion.equals(" ")) {
							System.out.println(excpetion + "expcetion");
							ObjectNode result = Json.newObject();
							result.put("status", "KO");
							result.put("message",excpetion);
							return badRequest(result);
						}
						else {
							String trans = XPath.selectText("//TransactionId", dom);
							b.payment.setTransactionId(trans);
							
							ObjectNode result = Json.newObject();
							result.put("TransactionId", trans);
							result.put("redirectUrl",REDIRECT_URL + "?merchantId=" + MERCHANT_ID  +"&transactionId="+trans);
							return ok(result);
						}
					}
				}
		);
		return resultPromise;
	}
	
	
	/**
	 * authenticatePayment should be called after user has filled in payment form at Netaxcept and redirected back
	 * This method should call nets and authenticate/reserve amount on given paymentId. 
	 * 
	 * @param paymentId id of payment returned by nets.
	 * @return Result with information about success/failure of payment
	 */
	public static Promise<Result> authenticatePayment() {
		JsonNode json = request().body().asJson();
		if(json == null) {
			return Promise.pure((Result)badRequest("Request contains no Json"));
		}
		
		String transactionId = json.get("transactionId").asText();
		if(transactionId == null) {
			return Promise.pure((Result)badRequest("No transactionId")); 
		}
		//check if p contains a booking (Check if the transactionId is a valid id at all.
		Payment p = Payment.find.where().eq("transactionId", transactionId).findUnique();
		if(p.booking.status.equals(Booking.TIMEDOUT)) {
			return Promise.pure((Result)badRequest("Request timed out")); 
		}
		
		String responseCode = json.get("responseCode").asText();
		if(!responseCode.equals("OK")) {
			return Promise.pure((Result)badRequest(responseCode)); 
		}
		
		//async  call to netAxcept
		final Promise<Result> resultPromise = WS.url(NETS_PROCESS)
				.setQueryParameter("merchantId", MERCHANT_ID)
				.setQueryParameter("token", SECRET_MERCHANT)
				.setQueryParameter("transactionId", transactionId)
				.setQueryParameter("operation", "AUTH")
				.get().map(
				new Function<WS.Response, Result>() {
					public Result apply(WS.Response response) {
						System.out.println(response.getBody());
						return ok(response.getBody());
					}
				}
				);
		return resultPromise;
	}
	
	
	
	/**
	 * Before set cancellation date, a user can cancel a booking. The cancelPayment should call nets and annul the reserved amount
	 * on users card, or credit user if payment is already captured.
	 * Depending on whether the payment has been captured or not, the amount can be annulled 
	 * or credited. 
	 * See http://www.betalingsterminal.no/Netthandel-forside/Teknisk-veiledning/Flow-Outline/
	 * @param paymentId
	 * @return boolean telling if operation was successful.
	 */
public static Promise<Boolean> cancelPayment(String transactionId) {
		
		//Temp: Assume a payment that is cancelled has not yet been captured

		final Promise<Boolean> resultPromise = WS.url(NETS_PROCESS)
				.setQueryParameter("merchantId", MERCHANT_ID)
				.setQueryParameter("token", SECRET_MERCHANT)
				.setQueryParameter("transactionId", transactionId)
				.setQueryParameter("operation", "ANNUL")
				.get().map(
				new Function<WS.Response, Boolean>() {
					public Boolean apply(WS.Response response) {
						//needs check here
						System.out.println(response.getBody());
						return true;
					}
				}
				);
		return resultPromise;
	}
}
