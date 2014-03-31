package controllers;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.util.encoders.Base64;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.User;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.libs.F;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.*;
import utilities.AESBouncyCastle;
import static play.libs.Json.toJson;
import static play.mvc.Controller.request;
import static play.mvc.Controller.response;

public class SecurityController extends Action.Simple {

	public final static String AUTH_TOKEN_HEADER = "X-AUTH-TOKEN";
	public static final String AUTH_TOKEN = "authToken";

	public Promise<SimpleResult> call(Http.Context ctx) throws Throwable {
		User user = null;
		String[] authTokenHeaderValues = ctx.request().headers().get(AUTH_TOKEN_HEADER);


		if ((authTokenHeaderValues != null) && (authTokenHeaderValues.length == 1) && (authTokenHeaderValues[0] != null)) {
			user = models.User.findByAuthToken(authTokenHeaderValues[0]);
			if (user != null) {
				ctx.args.put("user", user);
				return delegate.call(ctx);
			}
		}

		return F.Promise.pure((SimpleResult) unauthorized("unauthorized"));
	}

	public static User getUser() {
		return (User)Http.Context.current().args.get("user");
	}

	// returns an authToken
	public static Result login() {
		JsonNode json = request().body().asJson();
		
		/** TEST ENCRYPT TODO EXPAND 
		 * Currently this only encrypts a simple string "This is the secret message!" but this will
		 * invariably be replaced with JSON payloads. The messages between client (i.e. end-user) and DNT Connect
		 * is performed via our server. This has to run HTTPS as per DNT Connect guidelines, and the encryption below
		 * has to be done not by a keyGenerator but with the shared key we've got from lastpass. Next task should be to integrate
		 * with HTTPS branch and proof-of-concept a redirect URL from DNT Connect*/
		KeyGenerator kg;

		try {
			kg = KeyGenerator.getInstance("AES");
			kg.init(256);
			SecretKey sk = kg.generateKey();

			AESBouncyCastle abc = new AESBouncyCastle();
			abc.setPadding(new PKCS7Padding());
			abc.setKey(sk.getEncoded());

			String secret = "This is the secret message!";
			System.out.println(secret);
			byte[] ba = secret.getBytes("UTF-8");

			byte[] encr = abc.encrypt(ba);
			System.out.println("Encrypted: "+Base64.encode(encr));
			byte[] retr = abc.decrypt(encr);

			//does require foreknowledge of length to decrypt correctly. This should be stated during set-up communication maybe? Look into it TODO
			if(retr.length == ba.length) {
				ba = retr;
			} else {
				System.arraycopy(retr, 0, ba, 0, ba.length);
			}

			String decrypted = new String(ba, "UTF-8");
			System.out.println("Decrypted: "+decrypted); 
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException | DataLengthException | InvalidCipherTextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/** END TEST TODO EXPAND */
		
		//veldig dårlig måte å gjøre det på
		String emailAddress = json.get("emailAdress").asText();
		String password = json.get("password").asText();
		//burde kanskje gjøre det med forms?
		if(emailAddress == null || password == null) {
			return badRequest("No hope");
		}

		Login login = new Login();
		login.emailAddress = emailAddress;
		login.password = password;
		User user = User.findByEmailAddressAndPassword(login.emailAddress, login.password);

		if (user == null) {
			return unauthorized();
		}
		else {
			String authToken = user.createToken();
			ObjectNode authTokenJson = Json.newObject();
			authTokenJson.put(AUTH_TOKEN, authToken);
			response().setCookie(AUTH_TOKEN, authToken);
			return ok(authTokenJson);
		}
	}

	@With(SecurityController.class)
	public static Result logout() {
		response().discardCookie(AUTH_TOKEN);
		getUser().deleteAuthToken();
		return redirect("/");
	}

	public static class Login {

		@Constraints.Required
		@Constraints.Email
		public String emailAddress;

		@Constraints.Required
		public String password;

		public String toString() {
			return "login: " +emailAddress + password;
		}
	}


}