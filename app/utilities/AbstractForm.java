package utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.db.ebean.Model;
import play.libs.Json;

/**
 * A generic abstract form class that all custom form classes should extend.
 * The class contains the basic methods and fields
 *  to support binding and validation of json data.
 * to ensure that subclasses use the same pattern.
 * @author Olav
 *
 * @param <Model> All usage of form should bind to a subclass of ebean.Model.
 */
public abstract class AbstractForm<Model> {
	protected Boolean validationError;
	protected ObjectNode error;
	protected ObjectNode success;
	
	/**
	 * Method to construct a suitable json response error message
	 * @param message - a message describing problem.
	 */
	protected void addError(String message) {
		if(error == null) {
			error = Json.newObject();
			error.put("status", "KO");
		}
		error.put("message",  message);
		this.validationError = true;
	}
	
	/**
	 * Should return false if error during deserializing, validation or
	 * binding happens. The validate method is run the first time 
	 * the validationError is asked for. This ensures that this expensive method
	 * wont be run again.
	 * @return boolean flag about whether the form is valid or not
	 */
	public boolean isValid() {
		if(this.validationError == null) {
			this.validationError = !validate();
		}
		
		return !this.validationError;
	}
	
	/**
	 * Getter for error messages, used in response messages to the user.
	 * @return json string with error message and status
	 */
	public ObjectNode getError() {
		if(error == null) {
			error = Json.newObject();
			error.put("status", "KO");
		}
	
		return error;
	}
	
	/**
	 * returns a success json string containing status and message.
	 * @return
	 */
	public ObjectNode getSuccess() {
		if(success == null) {
			success = Json.newObject();
			success.put("status", "OK");
		}
		return success;
	}
	
	/**
	 * adds a key/value attribute to the json response string.
	 * 
	 * @param key
	 * @param value
	 */
	protected void addSuccess(String key, String value) {
		if(success == null) {
			success = Json.newObject();
			success.put("status", "OK");
		}
		success.put(key, value);
	}
	
	/**
	 * Method responsible for binding form to a model.
	 * @return <Model> subclass of ebean.Model
	 */
	public abstract Model createModel();
	
	/**
	 * Method responsible for validating form.
	 * @return a flag that tells whether the form is valid or not
	 */
	public abstract boolean validate();
	
}
