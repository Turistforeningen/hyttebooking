package controllers;



	


import org.w3c.dom.Document;


import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Booking;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.SimpleResult;
import static play.libs.F.Function;
import static play.libs.F.Promise;
import play.libs.F;
import play.libs.Json;
import play.libs.WS;

public class PaymentController extends Controller {
	private static final String SECRET_MERCHANT = play.Play.application().configuration().getString("application.merchantKey");
	private static final String NETS_REGISTER = "https://epayment-test.bbs.no/Netaxept/Register.aspx";
	private static final String MERCHANT_ID = play.Play.application().configuration().getString("application.merchantId");
	private static final String REDIRECT_URL = "https://epayment-test.bbs.no/Terminal/default.aspx";
	/**
	 * The register controller method are used for starting a payment using Netaxept.
	 * The purpose of the register call is to send all the data needed to complete a transaction to Netaxept servers.
	 * A call is made to Netaxept that registers a transaction and a transaction id are returned if contact is established.
	 * This transaction id is stored in the database, and a redirect url to a Netaxept payment site is returned to the user.
	 * 
	 * @param bookingId - id of booking 
	 * @return Response containing redirect url for payment.
	 */
	public static Promise<Result> registerPayment(Long bookingId) {
		
		Booking b = Booking.getBookingById(bookingId+ "");
		if(b == null || b.status == Booking.BOOKED) {
			return Promise.pure((Result) notFound("notfound"));
		}
		
		/*if(b.user != SecurityController.getUser()) {
			return Promise.pure((Result) notFound("This is not your booking"));
		}*/
		
		
		final Promise<Result> resultPromise = WS.url(NETS_REGISTER)
				.setQueryParameter("merchantId", MERCHANT_ID)
				.setQueryParameter("token", SECRET_MERCHANT)
				.setQueryParameter("orderNumber", "123456")
				.setQueryParameter("amount", "1000")
				.setQueryParameter("CurrencyCode", "NOK")
				.setQueryParameter("redirectUrl", "http://localhost:9000")
				.get().map(
				new Function<WS.Response, Result>() {
					public Result apply(WS.Response response) {
						String trans = response.asXml().getElementsByTagName("TransactionId").item(0).getTextContent();
						ObjectNode result = Json.newObject();
						result.put("TransactionId", trans);
						result.put("redirectUrl",REDIRECT_URL + "?merchantId=" + MERCHANT_ID  +"?transactionId="+trans);
						return ok(result);
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
	public static Promise<Result> authenticatePayment(Long paymentId) {
		//problem: need to know payment id- 
		String url = "";

		final Promise<Result> resultPromise = WS.url(url).get().map(
				new Function<WS.Response, Result>() {
					public Result apply(WS.Response response) {
						return ok("status of payment. Went through?");
					}
				}
				);
		return resultPromise;
	}
	
	
	/**
	 * Method will capture amount reserved with paymentId. The method calls nets and payment are 
	 * withdrawn from customers card. 
	 * @param paymentId
	 * @return
	 */
	public static Promise<Result> capturePayment(Long paymentId) {
		
		String url = "";

		final Promise<Result> resultPromise = WS.url(url).get().map(
				new Function<WS.Response, Result>() {
					public Result apply(WS.Response response) {
						return ok("captured payment?");
					}
				}
				);
		return resultPromise;
	}
	
	/**
	 * Before payment has been captured, a user can cancel a booking. The cancelPayment should call nets and annul the reserved amount
	 * for the booking. 
	 * @param paymentId
	 * @return
	 */
public static Promise<Result> cancelPayment(Long paymentId) {
		
		String url = "";

		final Promise<Result> resultPromise = WS.url(url).get().map(
				new Function<WS.Response, Result>() {
					public Result apply(WS.Response response) {
						return ok("anulled payment?");
					}
				}
				);
		return resultPromise;
	}
}
