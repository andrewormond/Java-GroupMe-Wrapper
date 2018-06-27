package github.adeo88.groupme.api;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

public class Message {

	// atachments array
	String source_guid;
	String sender_type;
	long created_at;
	String sender_id;
	boolean system;
	String avatar_url;
	// favorited_by array
	String group_id;
	String user_id;
	String name;
	String id;
	String text;

	public Message(JSONObject json) {
		//TODO: Load "attachments" array
		source_guid = GroupMeAPI.ReadJSONStringWithNull(json, "source_guid");
		sender_type = GroupMeAPI.ReadJSONStringWithNull(json, "sender_type");
		created_at = json.getLong("created_at");
		sender_id = GroupMeAPI.ReadJSONStringWithNull(json, "sender_id");
		system = json.getBoolean("system");
		avatar_url = GroupMeAPI.ReadJSONStringWithNull(json, "avatar_url");
		//favorited_by = GroupMeAPI.ReadJSONStringWithNull(json, "");
		group_id = GroupMeAPI.ReadJSONStringWithNull(json, "group_id");
		user_id = GroupMeAPI.ReadJSONStringWithNull(json, "user_id");
		name = GroupMeAPI.ReadJSONStringWithNull(json, "name");
		id = GroupMeAPI.ReadJSONStringWithNull(json, "id");
		text = GroupMeAPI.ReadJSONStringWithNull(json, "text");
	}

	public static Message[] interpretMessages(JSONArray jsonArray) {
		Message[] messages = new Message[jsonArray.length()];
		for (int i = 0; i < jsonArray.length(); i++) {
			messages[i] = new Message(jsonArray.getJSONObject(i));
		}
		return messages;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Message [source_guid=" + source_guid + ", sender_type=" + sender_type + ", created_at=" + created_at
				+ ", sender_id=" + sender_id + ", system=" + system + ", avatar_url=" + avatar_url + ", group_id="
				+ group_id + ", user_id=" + user_id + ", name=" + name + ", id=" + id + ", text=" + text + "]";
	}
	
	

}
