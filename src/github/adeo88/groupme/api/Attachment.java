package github.adeo88.groupme.api;

import org.json.JSONObject;

import github.adeo88.groupme.api.Message.AttachmentType;

public class Attachment {
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