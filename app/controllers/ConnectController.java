package controllers;

import java.io.UnsupportedEncodingException;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.joda.time.DateTime;
import org.joda.time.Instant;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import flexjson.JSON;
import play.libs.Json;
import play.libs.WS;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;
import utilities.AESBouncyCastle;


/**
 * Controller class for DNT Connect for logging users in.
 * 
 * @author Jama
 */
public class ConnectController extends Controller {
	private static final String SIGNON = "https://www.turistforeningen.no/connect/signon/";
	private static final String CLIENT = "Booking";
	//private static final String REDIRECT_URL TODO: Currently leaving out redirect in order to have default redirect url

	/**
	 * Method used for testing connection to DNT connect, currently not working due to paylad
	 * encryption not being complete.
	 * @return
	 */
	public static Promise<Result> testConnect() throws Base64DecodingException, DataLengthException, InvalidCipherTextException, UnsupportedEncodingException { 
		AESBouncyCastle aes = new AESBouncyCastle();

		String json = "{\"timestamp\": "+getTimeStamp()+"}";
		
		System.out.println(json);

		byte[] dataBytes = aes.encrypt(json.getBytes("UTF-8"));
		String data = new String(dataBytes, "UTF-8");

		final Promise<Result> resultPromise = WS.url(SIGNON).
				setQueryParameter("client", CLIENT).
				setQueryParameter("data", data).
				get().map(
						new Function<WS.Response, Result>() {
							public Result apply(WS.Response response) {
								System.out.println(response.getBody());
								return ok(response.getBody());
							}
						}
						);
		return resultPromise;
	}
	
	/** Handles user login, the "https://www.turistforeningen.no/connect/signon/" url is used
	 * The response is an encrypted JSON
	 * @response ("er_autentisert" : false) The user isn't authenticated, 
	 * @response ("er_autentisert" : true) The user was authenticated and has usable information
	 */
	public static Promise<Result> processLogin() throws DataLengthException, InvalidCipherTextException, UnsupportedEncodingException {
		AESBouncyCastle aes = new AESBouncyCastle(); /** The encryption helper class **/
		String json = "{\"timestamp\": "+getTimeStamp()+"}"; /** The JSON payload sent containing timestamp **/
		byte[] encryptedJson = aes.encrypt(json.getBytes("UTF-8")); /** Payload encrypted **/
		String data = new String(Base64.encode(encryptedJson)); /** Base64 encoding of encrypted payload **/
		
		final Promise<Result> resultPromise = WS.url(SIGNON).
				setQueryParameter("client", CLIENT).
				setQueryParameter("data", data).
				get().map(
						new Function<WS.Response, Result>() {
							public Result apply(WS.Response response) throws DataLengthException, InvalidCipherTextException, Base64DecodingException {
								//Here we decrypt the JSON
								AESBouncyCastle aes = new AESBouncyCastle();
								String encryptedJson = response.getBody();
								String decryptedJson = new String(aes.decrypt(Base64.decode(encryptedJson)));
								JsonNode json = Json.parse(decryptedJson);
								if(authenticated(json)) {
									
								} else {
									return unauthorized();
								}
								System.out.println(response.getBody());
								return ok(response.getBody());
							}
						}
						);
		return resultPromise;
	}

	/**
	 * Checks if "er_autentisert" : true
	 * @param json
	 * @return
	 */
	protected static boolean authenticated(JsonNode json) {
		return json.get("er_autentisert").asBoolean();
	}

	/** Returns timestamp for now UTC using JodaTime.Instant **/
	public static long getTimeStamp() {
		return new Instant().getMillis() / 1000;
	}
}
