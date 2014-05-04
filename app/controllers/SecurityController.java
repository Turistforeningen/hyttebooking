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
	
	public static Status DNTLogin(User user) {
		if(user != null) {
			String authToken = user.createToken();
			ObjectNode authTokenAndInfo= Json.newObject();
			authTokenAndInfo.put(AUTH_TOKEN, authToken);
			authTokenAndInfo.put("name", user.fullName);
			authTokenAndInfo.put("isAdmin", user.admin);
			response().setCookie(AUTH_TOKEN, authToken);
			System.out.println("authToken ");
			return ok(authTokenAndInfo);
		}
		else {
			return unauthorized();
		}
		
	}
	
	@With(SecurityController.class)
	public static Result logout() {
		response().discardCookie(AUTH_TOKEN);
		User u = getUser();
		if(u != null){
			u.deleteAuthToken();
		}
		System.out.println("log ut");
		return ok();
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