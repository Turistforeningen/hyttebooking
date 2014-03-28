package utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.db.ebean.Model;
import play.libs.Json;


public abstract class AbstractForm<Model> {
	protected boolean validationError;
	protected ObjectNode error;
	protected ObjectNode success;
	
	protected void addError(String message) {
		if(error == null) {
			error = Json.newObject();
			error.put("status", "KO");
		}
		error.put("message", message);
		this.validationError = true;
	}
	
	public boolean isValid() {
		return !this.validationError;
	}
	
	public ObjectNode getError() {
		if(error == null) {
			error = Json.newObject();
			error.put("status", "KO");
		}
	
		return error;
	}
	
	public ObjectNode getSuccess() {
		if(success == null) {
			success = Json.newObject();
			success.put("status", "OK");
		}
		return success;
	}
	
	protected void addSuccess(String key, String value) {
		if(success == null) {
			success = Json.newObject();
			success.put("status", "OK");
		}
		success.put(key, value);
	}
	
	public abstract Model createModel();
	public abstract boolean validate();
	
}
