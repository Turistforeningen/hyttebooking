package controllers;

import java.io.UnsupportedEncodingException;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.joda.time.DateTime;
import org.joda.time.Instant;

import com.fasterxml.jackson.databind.JsonNode;

import javax.xml.bind.DatatypeConverter;

import flexjson.JSON;
import play.libs.Json;
import play.libs.WS;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;
import utilities.AESBouncyCastle;
import utilities.Payload;

/**
 * Controller class for DNT Connect for logging users in.
 * 
 * @author Jama
 */
public class ConnectController extends Controller {
	private static final String SIGNON = "https://www.turistforeningen.no/connect/signon/";
	private static final String CLIENT = "Booking";
	private static final byte[] SECRETKEY = DatatypeConverter.parseBase64Binary(play.Play.application().configuration().getString("application.secretKey"));
	//private static final String REDIRECT_URL TODO: Currently leaving out redirect in order to have default redirect url

	/**
	 * Method used for testing connection to DNT connect, currently not working due to paylad
	 * encryption not being complete.
	 * @return
	 */
	public static Promise<Result> testConnect() throws Exception { 
		AESBouncyCastle aes = new AESBouncyCastle(SECRETKEY);

		String jsonString = "{\"timestamp\": "+getTimeStamp()+"}";
		System.out.println("Json: "+jsonString);
		
		Payload payload = aes.encrypt(jsonString.getBytes("UTF-8"));
		String data = DatatypeConverter.printBase64Binary(payload.getCipherText());
		System.out.println("encrJsonBase64: "+data);
		
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
	public static Promise<Result> processLogin() throws Exception {
		AESBouncyCastle aes = new AESBouncyCastle(SECRETKEY); /** The encryption helper class **/
		String jsonString = "{\"timestamp\": "+getTimeStamp()+"}"; /** The JSON payload to be sent containing timestamp **/
		Payload payload = aes.encrypt(jsonString.getBytes("UTF-8")); /** Payload encrypted **/
		String encrJson64 = DatatypeConverter.printBase64Binary(payload.getCipherText()); /** Base64 encoding of encrypted payload **/
		
		final Promise<Result> resultPromise = WS.url(SIGNON).
				setQueryParameter("client", CLIENT).
				setQueryParameter("data", encrJson64).
				get().map(
						new Function<WS.Response, Result>() {
							public Result apply(WS.Response response) throws Exception {
								AESBouncyCastle aes = new AESBouncyCastle(SECRETKEY);
								//Here we decrypt the JSON
								int ctLength = 0; //TODO WE NEED TO KNOW HOW LONG THE PLAINTEXT IS BEFORE DECRYPTING, ASK DNT!
								String encrJson64 = response.getBody();
								byte[] encrJson = DatatypeConverter.parseBase64Binary(encrJson64); //decode base64
								String jsonString = new String(aes.decrypt(ctLength, encrJson));
								JsonNode json = Json.parse(jsonString);
								if(authenticated(json)) {
									//user was authenticated, from here on we have a sherpa id to associate with our own user id
									//TODO
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
