package github.adeo88.groupme.api;

import java.util.HashMap;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Java Representation of the DirectMessage JSON Objects returned by the GroupMe
 * V3 API.
 * 
 * @author adeo8
 *
 */
public class DirectMessage extends Message {
	public String recipient_id;

	/**
	 * Constructs a DirectMessage from a JSONObject
	 * 
	 * @param json
	 *            JSONObject to interpret
	 */
	public DirectMessage(JSONObject json) {
		super(json);
		recipient_id = Utils.jsonReadString(json, "recipient_id");

	}

	/**
	 * Returns the associated conversation id of the message. This is useful when
	 * Message is overridden by DirectMessage, since DirectMessage does not have a
	 * Group.
	 * 
	 * @return The conversation ID
	 */
	@Override
	public String getConversationID() {
		return recipient_id;
	}

	/**
	 * Interprets a JSONArray of direct messages and converts them into a
	 * DirectMessage Array.
	 * 
	 * @param jsonArray
	 *            The JSONArray to interpret
	 * @return A DirectMessage Array
	 */
	public static DirectMessage[] interpretMessages(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new DirectMessage[0];
		}
		DirectMessage[] messages = new DirectMessage[jsonArray.length()];
		for (int i = 0; i < jsonArray.length(); i++) {
			messages[i] = new DirectMessage(jsonArray.getJSONObject(i));
		}
		return messages;
	}

	/**
	 * Sends a direct message to a recipients
	 * 
	 * @param recipient_id
	 *            The user_id of the other user
	 * @param text
	 *            The body of the message
	 * @param source_guid
	 *            The unique, client-side guid to attach to the message
	 * @param attachments
	 *            Attachments for the message
	 * @param api
	 *            The associated GroupMeAPI
	 * @return The DirectMessage sent
	 * @throws GroupMeException
	 *             If HTTP or JSON error has occured
	 */
	public static DirectMessage sendDirectMessage(String recipient_id, String text, String source_guid,
			JSONArray attachments, GroupMeAPI api) throws GroupMeException {
		JSONObject json = new JSONObject();
		json.put("text", text);
		json.put("source_guid", source_guid);
		json.put("recipient_id", recipient_id);
		if (attachments != null) {
			json.put("attachments", attachments);
		}
		JSONObject payload = new JSONObject();
		payload.put("direct_message", json);
		JSONObject response = api.sendPostRequest("/direct_messages", payload.toString(), true);
		return new DirectMessage(response.getJSONObject("response").getJSONObject("direct_message"));

	}

	/**
	 * Contacts the API and downloads the 20 most recent direct messages associated
	 * with the other user.
	 * 
	 * @param other_user_id
	 *            The user_id of the other user
	 * @param api
	 *            The GroupMeAPI that contains the token
	 * @return The DirectMessages returned from the server
	 * @throws GroupMeException
	 *             If a HTTP or JSON error occurs
	 */
	public static DirectMessage[] index(String other_user_id, GroupMeAPI api) throws GroupMeException {
		return index(other_user_id, Optional.empty(), Optional.empty(), api);
	}

	/**
	 * Contacts the API and downloads a specified range of direct messages
	 * associated with the other user.
	 * 
	 * @param other_user_id
	 *            The user_id of the other user
	 * @param before_id
	 *            Optional String id of the latest message to fetch
	 * @param since_id
	 *            Optional String id of the first message to fetch
	 * @param api
	 *            The GroupMeAPI that contains the token
	 * @return The DirectMessages returned from the server
	 * @throws GroupMeException
	 *             If a HTTP or JSON error occurs
	 */
	public static DirectMessage[] index(String other_user_id, Optional<String> before_id, Optional<String> since_id,
			GroupMeAPI api) throws GroupMeException {
		HashMap<String, String> parameters = new HashMap<>();
		parameters.put("other_user_id", other_user_id);
		if (before_id.isPresent()) {
			parameters.put("before_id", before_id.get());
		}
		if (since_id.isPresent()) {
			parameters.put("since_id", since_id.get());
		}
		JSONObject jsonPackage = api.sendGetRequest("/direct_messages", parameters, true).getJSONObject("response");
		DirectMessage[] messages = DirectMessage.interpretMessages(jsonPackage.getJSONArray("direct_messages"));
		return messages;
	}

}
