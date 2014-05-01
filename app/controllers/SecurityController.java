package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.User;
import play.data.validation.Constraints;
import play.libs.F;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.*;
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
	
	public static Status DNTLogin(User user) {
		String authToken = user.createToken();
		ObjectNode authTokenAndName= Json.newObject();
		authTokenAndName.put(AUTH_TOKEN, authToken);
		authTokenAndName.put("name", user.fullName);
		response().setCookie(AUTH_TOKEN, authToken);
		return ok(authTokenAndName);
	}

	@With(SecurityController.class)
	public static Result logout() {
		response().discardCookie(AUTH_TOKEN);
		User u = getUser();
		if(u == null){
			u.deleteAuthToken(); //won't this always be null? TODO @Olav
		}
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