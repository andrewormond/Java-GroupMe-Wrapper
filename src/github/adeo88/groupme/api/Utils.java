package github.adeo88.groupme.api;

import org.json.JSONArray;
import org.json.JSONObject;

import github.adeo88.groupme.api.Message.Attachment;

public class Utils {

	private Utils() {
	}

	static String jsonReadString(JSONObject json, String key) {
		if (json.has(key) && json.get(key) != org.json.JSONObject.NULL) {
			return json.getString(key);
		}
		return null;
	}

	static JSONObject jsonReadJSOBObject(JSONObject json, String key) {
		if (json.has(key) && json.get(key) != org.json.JSONObject.NULL) {
			return json.getJSONObject(key);
		}
		return null;
	}

	static int responseToCode(JSONObject response) {
		JSONObject meta = Utils.jsonReadJSOBObject(response, "meta");
		if (meta == null) {
			return -1;
		} else {
			return meta.getInt("code");
		}
	}

	static String[] interpretStrings(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new String[0];
		}
		String[] result = new String[jsonArray.length()];
		for (int i = 0; i < jsonArray.length(); i++) {
			result[i] = jsonArray.getString(i);
		}
		return result;
	}

}
