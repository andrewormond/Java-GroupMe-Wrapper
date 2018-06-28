package github.adeo88.groupme.api;

import org.json.JSONObject;

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
		if(meta == null) {
			return -1;
		}else {
			return meta.getInt("code");
		}
	}

}
