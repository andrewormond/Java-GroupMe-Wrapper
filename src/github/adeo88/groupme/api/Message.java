package github.adeo88.groupme.api;

import org.json.JSONArray;
import org.json.JSONObject;

public class Message {

	public enum AttachmentType {
		image, location, split, emoji, mentions, poll
	}

	public static class Attachment {
		public final AttachmentType type;
		public JSONObject data;

		public Attachment(AttachmentType type, JSONObject data) {
			this.type = type;
			this.data = data;
		}

		public Attachment(JSONObject json) {
			type = AttachmentType.valueOf(json.getString("type"));
			this.data = json;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Attachment [type=" + type + ", data=" + data + "]";
		}

	}

	public Attachment[] attachments;
	public String source_guid;
	public String sender_type;
	public long created_at;
	public String sender_id;
	public boolean system;
	public String avatar_url;
	public String[] favorited_by;
	public String group_id;
	public String user_id;
	public String name;
	public String id;
	public String text;

	public Message(JSONObject json) {
		attachments = Message.interpretAttachments(json.getJSONArray("attachments"));
		source_guid = Utils.jsonReadString(json, "source_guid");
		sender_type = Utils.jsonReadString(json, "sender_type");
		created_at = json.getLong("created_at");
		sender_id = Utils.jsonReadString(json, "sender_id");
		system = json.has("system") ? json.getBoolean("system") : false;
		avatar_url = Utils.jsonReadString(json, "avatar_url");
		this.favorited_by = Utils.interpretStrings(json.getJSONArray("favorited_by"));
		group_id = Utils.jsonReadString(json, "group_id");
		user_id = Utils.jsonReadString(json, "user_id");
		name = Utils.jsonReadString(json, "name");
		id = Utils.jsonReadString(json, "id");
		text = Utils.jsonReadString(json, "text");
	}

	public static Attachment[] interpretAttachments(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new Attachment[0];
		}
		Attachment[] result = new Attachment[jsonArray.length()];
		for (int i = 0; i < jsonArray.length(); i++) {
			result[i] = new Attachment(jsonArray.getJSONObject(i));
		}
		return result;
	}

	public static Message[] interpretMessages(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new Message[0];
		}
		Message[] messages = new Message[jsonArray.length()];
		for (int i = 0; i < jsonArray.length(); i++) {
			messages[i] = new Message(jsonArray.getJSONObject(i));
		}
		return messages;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Message [\"" + this.text + "\", created by: \"" + this.name + "\" in group: " + this.group_id + " with "
				+ this.attachments.length + " attachments and " + this.favorited_by.length + " likes]";
	}

	protected String getConversationID() {
		return this.group_id;
	}

	public int like(GroupMeAPI api) throws GroupMeException {
		return Utils.responseToCode(
				api.sendPostRequest("/messages/" + this.getConversationID() + "/" + this.id + "/like", "", true));
	}
	
	public int unlike(GroupMeAPI api) throws GroupMeException {
		return Utils.responseToCode(
				api.sendPostRequest("/messages/" + this.getConversationID() + "/" + this.id + "/unlike", "", true));
	}

}
