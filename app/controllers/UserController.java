package controllers;

import models.Booking;
import models.Cabin;
import models.User;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;

@With(SecurityController.class)
public class UserController extends Controller {

	/**
	 * Change the users firstName lastName
	 * @return
	 */
	public static Result changeName() {
		ObjectNode result = Json.newObject();
		JsonNode json = request().body().asJson();
		if(json == null) {
			result.put("status", "KO");
			result.put("message", "Expected Json");
			return badRequest(result);
		}
		else {
			User tempUser = User.findByEmailAddress(login.emailAddress);
			String nrPerson = json.get("nrOfPersons").asText();

			booking.save();
			result.put("status", "OK");
			result.put("message", "booking saved");
			return ok(result);
		}
	     if (user == null) {
	            return unauthorized();
		}
		return null; //TODO
	}


	/**
	 * Controller for changing users date of birth
	 * @return 
	 */
	public static Result changeDOB() {

		return null; //TODO
	}

	/**
	 * Controller for changing address belonging to user
	 * @return
	 */
	public static Result changeAddress() {

		return null; //TODO
	}

	/**
	 * Controller for changing email belonging to user
	 * @return
	 */
	public static Result changeEmail() {

		return null; //TODO
	}

	/**
	 * Controller for changing password belong to user
	 * @return
	 */
	public static Result changePassword() {

		return null; //TODO
	}
}
