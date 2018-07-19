package github.adeo88.groupme.api.events;

import java.util.Date;

import org.json.JSONObject;

import github.adeo88.groupme.api.Message;

public class MessageEvent {
	public final String alert;
	public final Date recievedAt;
	public final Message message;

	public MessageEvent(JSONObject data) {
		alert = data.getString("alert");
		recievedAt = new Date(data.getLong("received_at"));
		message = new Message(data.getJSONObject("subject"));
	}

	@Override
	public String toString() {
		return "MessageEvent [" + (alert != null ? "alert=\"" + alert + "\", " : "") + "recieved at " + recievedAt + ", "
				+ (message != null ? "message=" + message : "") + "]";
	}
	
	
}
