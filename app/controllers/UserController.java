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
	 * Change the users details 
	 * Fields on site should be called:
	 * - newName
	 * - newDOB
	 * - newEmail
	 * - newPassword
	 * - newAddress
	 * - newCity
	 * - newZipCode
	 * @return
	 */
	public static Result changeDetail() {
		ObjectNode result = Json.newObject();
		JsonNode json = request().body().asJson();
		if(json == null) {
			result.put("status", "KO");
			result.put("message", "Expected Json");
			return badRequest(result);
		}
		else {
			User user = SecurityController.getUser();
			if (user == null) {
				return unauthorized();
			}

			String newName = json.get("newName").asText(); //TODO ensure its called newName
			//TODO SECURITY //TODO tests
			user.fullName = newName;
			user.save();
			
			//TODO add the remaining fields

			result.put("status", "OK");
			result.put("message", "booking saved");
			return ok(result);
		}
	}


}
