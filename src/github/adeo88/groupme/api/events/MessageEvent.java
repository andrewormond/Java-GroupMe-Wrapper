package github.adeo88.groupme.api.events;

import java.util.Date;

import org.json.JSONObject;

import github.adeo88.groupme.api.Message;

public class MessageEvent {
	public final String alert;
	public final Date recievedAt;
	public final Message message;
	public final JSONObject messageJSON;

	public MessageEvent(JSONObject data) {
		alert = data.getString("alert");
		recievedAt = new Date(data.getLong("received_at"));
		messageJSON = data.getJSONObject("subject");
		message = new Message(messageJSON);
	}

	@Override
	public String toString() {
		return "MessageEvent [" + (alert != null ? "alert=\"" + alert + "\", " : "") + "recieved at " + recievedAt + ", "
				+ (message != null ? "message=" + message : "") + "]";
	}
	
	
}
