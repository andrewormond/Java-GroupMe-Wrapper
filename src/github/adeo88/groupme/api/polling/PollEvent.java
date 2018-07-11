package github.adeo88.groupme.api.polling;

import java.util.Date;

import org.json.JSONObject;

import github.adeo88.groupme.api.Group;
import github.adeo88.groupme.api.Message;
import github.adeo88.groupme.api.Utils;
import github.adeo88.groupme.api.polling.PollEvent.PollEventType;

public class PollEvent {
	public enum PollEventType {
		typing("typing"),
		favorite("favorite"),
		create("line.create"),
		unknown("");
		
		public String raw;
	
		private PollEventType(String raw) {
			this.raw = raw;
		}
		
		public static PollEventType getType(String raw) {
			raw = raw.trim();
			for(PollEventType typ : PollEventType.values()) {
				if(raw.equals(typ.raw)) {
					return typ;
				}
			}
			return unknown;
		}
	}

	public final PollEvent.PollEventType type;
	public Message msg;
	public String user_id;
	public String group_id;
	public String time;
	public final JSONObject payload;
	
	public PollEvent(String raw, JSONObject payload) {
		this.payload = payload;
		this.type = PollEvent.PollEventType.getType(raw);
		switch(this.type) {
		case favorite:
			JSONObject subject = payload.getJSONObject("data").getJSONObject("subject");
			this.msg = new Message(subject.getJSONObject("line"));
			this.user_id = subject.getString("user_id");
			break;
		case typing:
			this.group_id = payload.getString("channel").substring("/group/".length());
			JSONObject info = payload.getJSONObject("data");
			this.user_id = info.getString("user_id");
			this.time = Utils.dateFormatter.format(new Date(info.getLong("started")));
			break;
		case create:
			this.msg = new Message(payload.getJSONObject("data").getJSONObject("subject"));
			break;
		default:
			break;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GroupEvent [" + (type != null ? "type=" + type + ", " : "")
				+ (msg != null ? "msg=" + msg + ", " : "") + (user_id != null ? "user_id=" + user_id + ", " : "")
				+ (group_id != null ? "group_id=" + group_id + ", " : "")
				+ (time != null ? "time=" + time + ", " : "") + (type == PollEvent.PollEventType.unknown ? "payload=" + payload : "") + "]";
	}
	
	
}