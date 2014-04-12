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
import play.i18n.Messages;
import play.libs.F;
import play.libs.Json;
import play.libs.WS;
import play.libs.XPath;
import utilities.JsonMessage;

public class PaymentController extends Controller {
	private static final String BASE_URL = "https://test.epayment.nets.eu/Netaxept/";
	private static final String SECRET_MERCHANT = play.Play.application().configuration().getString("application.merchantKey");
	private static final String NETS_REGISTER = BASE_URL +"Register.aspx";
	private static final String NETS_PROCESS = BASE_URL + "Process.aspx";
	private static final String NETS_QUERY = BASE_URL + "query.aspx";
	private static final String MERCHANT_ID = play.Play.application().configuration().getString("application.merchantId");
	private static final String REDIRECT_URL = "https://test.epayment.nets.eu/Terminal/default.aspx";
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
			return Promise.pure((Result) notFound(JsonMessage.error(Messages.get("payment.bookingNotFound"))));
		}
		//if deliverydate is 3 month in the future nets wont collect it automatically,
		//Set deliveryDate to 3 month in the future if booking happends i.e 2 years from now.
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
						System.out.println(response.getBody());
						
						Document dom = response.asXml();

						
						if(dom == null) {
							return badRequest(JsonMessage.error("Cant get proper response from nets"));
						}
						
						String exception = XPath.selectText("//Exception", dom);
						if(exception.equals("")) {
							String trans = XPath.selectText("//TransactionId", dom);
							b.payment.setTransactionId(trans);
	
							ObjectNode result = Json.newObject();
							result.put("TransactionId", trans);
							result.put("redirectUrl",REDIRECT_URL + "?merchantId=" + MERCHANT_ID  +"&transactionId="+trans);
							return ok(result);
						}
						else {
							return badRequest(JsonMessage.error(exception));	
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
			return Promise.pure((Result)badRequest(JsonMessage.error(Messages.get("json.expected"))));
		}
		
		String transactionId = json.get("transactionId").asText();
		if(transactionId == null) {
			return Promise.pure((Result)badRequest(JsonMessage.error("No transactionId sent"))); 
		}
		//check if p contains a booking (Check if the transactionId is a valid id at all.
		Payment p = Payment.find.where().eq("transactionId", transactionId).findUnique();
		if(p.booking.status.equals(Booking.TIMEDOUT)) {
			return Promise.pure((Result)badRequest(JsonMessage.error("You took to long to pay, booking timed out"))); 
		}
		
		String responseCode = json.get("responseCode").asText();
		if(!responseCode.equals("OK")) {
			return Promise.pure((Result)badRequest(JsonMessage.error("Problem with payment, cannot authenticate payment"))); 
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

								Document dom = response.asXml();

								if(dom == null) {
									return badRequest(JsonMessage.error("Cant get proper response from nets"));
								}

								String exception = XPath.selectText("//Exception", dom);
								if(exception.equals("")) {
									return ok(JsonMessage.success("payment authenicated"));
								}
								else {
									return badRequest(JsonMessage.error(XPath.selectText("//Message", dom)));

								}
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
		//should probably do this async and set flag here.
	try {
		Document dom = queryPayment(transactionId).get(10000);
		String amountCaptured = XPath.selectText("//AmountCaptured", dom);
		System.out.println(amountCaptured +" lol");
		//If anything has been captured already, credit customer, else annul order
		if(Integer.parseInt(amountCaptured)>0) {
			return authOrCreditPayment(transactionId, "CREDIT");
		}
		else {
			return authOrCreditPayment(transactionId, "ANNUL");
		}
	} catch (Exception e) {
	}
		//booking controller does not take this return value into consideration
		return null;
		
}

/**
 * Call nets about a transaction and get information about transaction.
 * Who paid, when, has it been annulled, captured etc
 * @param transactionId - transaction to query about
 * @return Document - xml response from Nets
 */
public static Promise<Document> queryPayment(String transactionId) {
	final Promise<Document> resultPromise = WS.url(NETS_QUERY)
			.setQueryParameter("merchantId", MERCHANT_ID)
			.setQueryParameter("token", SECRET_MERCHANT)
			.setQueryParameter("transactionId", transactionId)
			.get().map(
			new Function<WS.Response, Document>() {
				public Document apply(WS.Response response) {
					System.out.println(response.getBody());
					Document dom = response.asXml();
					return dom;
				}
			}
			);
	return resultPromise;
}

/**
 * Depending on whether payment has been captured or not will either credit or annul a payment.
 * @param transactionId - booking transaction to be refunded
 * @param operation - either ANNUL or CREDIT
 * @return a boolean telling whether operation went ok or not.
 */
public static Promise<Boolean> authOrCreditPayment(String transactionId, String operation) {
	final Promise<Boolean> resultPromise = WS.url(NETS_PROCESS)
			.setQueryParameter("merchantId", MERCHANT_ID)
			.setQueryParameter("token", SECRET_MERCHANT)
			.setQueryParameter("transactionId", transactionId)
			.setQueryParameter("operation", operation)
			.get().map(
			new Function<WS.Response, Boolean>() {
				public Boolean apply(WS.Response response) {
					//needs check here
					System.out.println(response.getBody());
					
					Document dom = response.asXml();

					if(dom == null) {
						return false;
					}

					String exception = XPath.selectText("//Exception", dom);
					if(exception.equals("")) {
						return true;
					}
					else {
						return false;

					}
					
				}
			}
			);
	return resultPromise;
}
}
