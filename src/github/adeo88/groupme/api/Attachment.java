package github.adeo88.groupme.api;

import org.json.JSONArray;
import org.json.JSONObject;

import github.adeo88.groupme.api.Attachment.AttachmentType;

public class Attachment {
	public enum AttachmentType {
		image, location, split, emoji, mentions, poll
	}

	public final Attachment.AttachmentType type;
	public JSONObject data;

	public Attachment(Attachment.AttachmentType type, JSONObject data) {
		this.type = type;
		this.data = data;
	}

	public Attachment(JSONObject json) {
		type = Attachment.AttachmentType.valueOf(json.getString("type"));
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

	/**
	 * Converts a JSONArray to a Attachment Array
	 * @param jsonArray JSONArray to interpret
	 * @return an Attachment Array
	 */
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

}