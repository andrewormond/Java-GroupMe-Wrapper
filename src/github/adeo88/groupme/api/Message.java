package github.adeo88.groupme.api;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Java Representation of the Message JSON Objects returned by the GroupMe V3
 * API.
 * 
 * @author adeo8
 *
 */
public class Message {

	/**
	 * List of attachments associated with the Message
	 */
	public Attachment[] attachments;

	/**
	 * The source_guid associated with the Message
	 */
	public String source_guid;

	/**
	 * The type of sender. (Usually either "system" or "user")
	 */
	public String sender_type;

	/**
	 * Time this Message was originally created in the Unix epoch format, which is
	 * the number of seconds since January 1, 1970 in the UTC (GMT) timezone.
	 */
	public long created_at;

	/**
	 * The member_id of the sender. This is not the user_id.
	 *
	 */
	public String sender_id;

	/**
	 * Flag indicating if it was a system message.
	 */
	public boolean system;

	/**
	 * The URL for this User's Avatar associated with this Message. May be null if
	 * user has not set an Avatar yet.
	 */
	public String avatar_url;

	/**
	 * List of member_id's that have liked the message
	 */
	public String[] favorited_by;

	/**
	 * group id associated with the message
	 */
	public String group_id;

	/**
	 * user_id associated with the poster of the message
	 */
	public String user_id;

	/**
	 * nickname of the associated user
	 */
	public String name;

	/**
	 * Message id of the associated message
	 */
	public String id;

	/**
	 * Body of the message
	 */
	public String text;

	/**
	 * Constructs a Message from a JSONObject.
	 * 
	 * @param json
	 *            JSONObject to interpret
	 */
	public Message(JSONObject json) {
		attachments = Attachment.interpretAttachments(json.getJSONArray("attachments"));
		source_guid = Utils.jsonReadString(json, "source_guid");
		sender_type = Utils.jsonReadString(json, "sender_type");
		created_at = json.getLong("created_at");
		sender_id = Utils.jsonReadString(json, "sender_id");
		system = json.has("system") ? json.getBoolean("system") : false;
		avatar_url = Utils.jsonReadString(json, "avatar_url");
		if (json.has("favorited_by")) {
			this.favorited_by = Utils.interpretStrings(json.getJSONArray("favorited_by"));
		}else {
			this.favorited_by = new String[0];
		}
		group_id = Utils.jsonReadString(json, "group_id");
		user_id = Utils.jsonReadString(json, "user_id");
		name = Utils.jsonReadString(json, "name");
		id = Utils.jsonReadString(json, "id");
		text = Utils.jsonReadString(json, "text");
	}

	/**
	 * Interprets a JSONArray of messages and converts them into a Message Array.
	 * 
	 * @param jsonArray
	 *            The JSONArray to interpret
	 * @return A Message Array
	 */
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

	/**
	 * Returns a readable String representation of the Message
	 * 
	 */
	@Override
	public String toString() {
		return "Message [\"" + this.text + "\", created by: \"" + this.name + "\" in group: " + this.group_id + " with "
				+ this.attachments.length + " attachments and " + this.favorited_by.length + " likes]";
	}

	/**
	 * Returns the associated conversation id of the message. This is useful when
	 * Message is overridden by DirectMessage, since DirectMessage does not have a
	 * Group.
	 * 
	 * @return The Conversation ID
	 */
	public String getConversationID() {
		return this.group_id;
	}

	/**
	 * Likes/Favorites the message.
	 * 
	 * @param api
	 *            GroupMeAPI whose token is used
	 * @return HTTP Status Code (200: OK)
	 * @throws GroupMeException
	 *             if an HTTP of JSON Error occurs
	 */
	public int like(GroupMeAPI api) throws GroupMeException {
		return Utils.responseToCode(
				api.sendPostRequest("/messages/" + this.getConversationID() + "/" + this.id + "/like", "", true));
	}

	/**
	 * Un-likes/un-favorites the message.
	 * 
	 * @param api
	 *            GroupMeAPI whose token is used
	 * @return HTTP Status Code (200: OK)
	 * @throws GroupMeException
	 *             if an HTTP of JSON Error occurs
	 */
	public int unlike(GroupMeAPI api) throws GroupMeException {
		return Utils.responseToCode(
				api.sendPostRequest("/messages/" + this.getConversationID() + "/" + this.id + "/unlike", "", true));
	}

}
