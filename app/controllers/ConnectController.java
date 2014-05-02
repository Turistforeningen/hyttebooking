package controllers;

import java.util.Arrays;

import models.User;

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
 */
public class ConnectController extends Controller {
	private static final String REDIRECT_URL = "http://localhost:9000/dev#/login";
	private static final String CLIENT = "?client=hyttebooking";
	private static final String SIGNON = "https://www.turistforeningen.no/connect/signon/" +CLIENT + "&data=";
	private static final byte[] SECRETKEY = DatatypeConverter.parseBase64Binary(play.Play.application().configuration().getString("application.secretKey"));
	private static final long ADMIN_ID = getAdminId();
	
	public static String EncodeURL(String url) throws java.io.UnsupportedEncodingException {
	    return java.net.URLEncoder.encode(url, "UTF-8");
	}
	
	public static String DecodeURL(String url) throws java.io.UnsupportedEncodingException {
		return java.net.URLDecoder.decode(url, "UTF-8");
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
		data.put("redirect_url", REDIRECT_URL);
		byte[] encr = aes.encrypt(data.toString().getBytes("UTF-8")); /** Payload encrypted **/
		String encrJson64 = DatatypeConverter.printBase64Binary(encr); /** Base64 encoding of encrypted payload **/
		String hmac = aes.sha512AndBase64(aes.getIvAndPlainText());
		
		String testP	= new String(Arrays.copyOfRange(aes.getIvAndPlainText(), 16, encr.length));//TODO remove
		String tesIv	= DatatypeConverter.printBase64Binary(Arrays.copyOfRange(aes.getIvAndPlainText(), 0, 16));
		System.out.println("################ iv\t\t "+tesIv);
		System.out.println("################ plaintext\t"+testP);
		System.out.println("FINAL data: "+encrJson64);
		System.out.println("FINAL hash: "+hmac);
		ObjectNode retNode = Json.newObject();
		retNode.put("redirectUrl", ""+SIGNON+EncodeURL(encrJson64)+"&hmac="+EncodeURL(hmac));
		return ok(retNode);
	}
	
	/**
	 * Two query parameters gotten, data and hmac
	 * Decrypt data
	 * Check if HMAC(iv+plainText) == hmac
	 * Turn plainText into json object 
	 * @return
	 * @throws Exception 
	 */
	public static Result checkLogin() throws Exception {
		System.out.println();
		System.out.println("---------------------------");
		System.out.println("---------------------------");
		
		String dataB64 = request().body().asJson().get("data").asText();
		String hmacB64 = request().body().asJson().get("hmac").asText();
		
		byte[] data = DatatypeConverter.parseBase64Binary(dataB64);
		byte[] hmac = DatatypeConverter.parseBase64Binary(hmacB64);
		
		AESBouncyCastle aes = new AESBouncyCastle(SECRETKEY);
		byte[] plainText = aes.decrypt(data, hmac);
		System.out.println("PLAINTEXT" + plainText);
		JsonNode login = Json.parse(new String(plainText, "UTF-8"));
		System.out.println("LOGIN RECIEVED: #########");
		System.out.println(login.asText());
		System.out.println("END LOGIN ##########");
		
		if(!login.get("er_autentisert").asBoolean())
			return unauthorized(); //TODO maybe need better response
		
		long id 		= login.get("sherpa_id").asLong();
		String email 	= login.get("epost").asText();
		String fName 	= login.get("fornavn").asText();
		String lName	= login.get("etternavn").asText();
		System.out.println(id + "email" + email+  "fName" + "lName");
		User user = User.findBySherpaId(id);
		if(user == null) { //first time using booking solution, we need to register user internally
			user = new User(id, email, fName+" "+lName); //TODO don't split fName and lName
			if(id == ADMIN_ID) {
				user.admin = true;
			}
			user.save();
		}
		return SecurityController.DNTLogin(user);
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
	
	private static long getAdminId() {
		long sherpaId = 0;
		try {
			sherpaId = play.Play.application().configuration().getLong("application.adminSherpaId");
		} catch (Exception e) {
		}
		return sherpaId;
	}
}
