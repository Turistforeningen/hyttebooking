package controllers;

import org.joda.time.Instant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.xml.bind.DatatypeConverter;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utilities.AESBouncyCastle;

/**
 * Controller class for DNT Connect for logging users in.
 * 
 * @author Jama
 */
public class ConnectController extends Controller {
	private static final String CLIENT = "?client=hyttebooking";
	private static final String SIGNON = "https://www.turistforeningen.no/connect/signon/" +CLIENT + "&data=";
	private static final byte[] SECRETKEY = DatatypeConverter.parseBase64Binary(play.Play.application().configuration().getString("application.secretKey"));
	//private static final String REDIRECT_URL TODO: Currently leaving out redirect in order to have default redirect url
	
	public static String EncodeURL(String url) throws java.io.UnsupportedEncodingException {
	    url = java.net.URLEncoder.encode(url, "UTF-8");
	    return url;
	}
	
	/** Handles user login, the "https://www.turistforeningen.no/connect/signon/" url is used
	 * The response is an encrypted JSON
	 * @response ("er_autentisert" : false) The user isn't authenticated, 
	 * @response ("er_autentisert" : true) The user was authenticated and has usable information
	 */
	public static Result setupLogin() throws Exception {
		AESBouncyCastle aes = new AESBouncyCastle(SECRETKEY); /** The encryption helper class **/
		ObjectNode data = Json.newObject();
		data.put("timestamp", getTimeStamp()); //not containing redirect URL right now, add "put("redirect_url", getRedirectUrl()" as needed
		System.out.println(data.asText());
		byte[] encr = aes.encrypt(data.asText().getBytes("UTF-8")); /** Payload encrypted **/
		String encrJson64 = DatatypeConverter.printBase64Binary(encr); /** Base64 encoding of encrypted payload **/
		
		ObjectNode retNode = Json.newObject();
		retNode.put("redirectUrl", ""+SIGNON+EncodeURL(encrJson64));
		return ok(retNode);
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
