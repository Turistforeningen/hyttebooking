package utilities;

import play.libs.Json;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Helper class for creation of json error and success messages to be returned
 * to user.
 * @author Olav
 *
 */
public class JsonMessage {
	/**
	 * General json error message containing status and message
	 * @param message - what error has occured
	 * @return ObjectNode containing json
	 */
	public static ObjectNode error(String message) {
		ObjectNode result = Json.newObject();
		result.put("status", "KO");
		result.put("message",message);
		return result;
	}
	/**
	 * General json success message containing status and message
	 * @param message - what went ok
	 * @return ObjectNode containing json
	 */
	public static ObjectNode success(String message) {
		ObjectNode result = Json.newObject();
		result.put("status", "OK");
		result.put("message",message);
		return result;
	}
	
	public static ObjectNode successWithId(String message, Long id) {
		ObjectNode result = success(message);
		result.put("id", id);
		return result;
	}
}
