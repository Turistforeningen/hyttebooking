package controllers;


import play.mvc.Controller;
import play.mvc.Result;
import static play.libs.F.Function;
import static play.libs.F.Promise;
import play.libs.WS;

public class PaymentController extends Controller {

	public static Promise<Result> register(Long bookingId) {
		String url = "https://epayment-test.bbs.no/Netaxept/Register.aspx?MerchantId=319102&token=secret&orderNumber=10011&amount=200&CurrencyCode=NOK"+
			"&redirectUrl=http://webshop/RegisterReply.asp";
		final Promise<Result> resultPromise = WS.url(url).get().map(
				new Function<WS.Response, Result>() {
					public Result apply(WS.Response response) {
						return ok(response.getBody());
					}

				}
		);
		
		return resultPromise;
	}
}
