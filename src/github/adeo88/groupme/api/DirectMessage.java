package github.adeo88.groupme.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

public class DirectMessage extends Message {
	public String recipient_id;

	public DirectMessage(JSONObject json) {
		super(json);
		recipient_id = Utils.jsonReadString(json, "recipient_id");

	}

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

	@Override
	public String toString() {
		return "Direct Message [\"" + this.text + "\", send by: \"" + this.name + "\" to user: " + this.recipient_id
				+ " with " + this.attachments.length + " attachments and " + this.favorited_by.length + " likes]";
	}

	@Override
	protected String getConversationID() {
		return recipient_id;
	}

	public static DirectMessage[] index(String other_user_id, GroupMeAPI api) throws GroupMeException {
		return index(other_user_id, Optional.empty(), Optional.empty(), api);
	}
	
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
