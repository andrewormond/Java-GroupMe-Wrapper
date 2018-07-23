package github.adeo88.groupme.api;

import org.json.JSONObject;

public class SMSMode {

	private SMSMode() {
	}

	public static int enableSMS(int hours_enabled, boolean suppress, GroupMeAPI api) throws GroupMeException {
		JSONObject payload = new JSONObject();
		payload.put("duration", hours_enabled);
		if (suppress) {
			payload.put("registration_id", api.token);
		}
		return Utils.responseToCode(api.sendPostRequest("/users/sms_mode", payload.toString(), true));
	}

	public static int disableSMS(GroupMeAPI api) throws GroupMeException {
		return Utils.responseToCode(api.sendPostRequest("/users/sms_mode/delete", "", true));
	}
}
