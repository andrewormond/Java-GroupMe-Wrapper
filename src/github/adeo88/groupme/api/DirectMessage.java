package github.adeo88.groupme.api;

import org.json.JSONArray;
import org.json.JSONObject;

public class DirectMessage extends Message {
	public String recipient_id;

	public DirectMessage(JSONObject json) {
		super(json);
		recipient_id = Utils.jsonReadString(json, "recipient_id");
		
	}
	
	public static DirectMessage sendDirectMessage(String recipient_id, String text, String source_guid, JSONArray attachments, GroupMeAPI api)
			throws GroupMeException {
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
		return "Direct Message [\"" + this.text + "\", send by: \"" + this.name + "\" to user: " + this.recipient_id + " with "
				+ this.attachments.length + " attachments and " + this.favorited_by.length + " likes]";
	}


}
