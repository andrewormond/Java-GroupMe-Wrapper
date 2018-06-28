package github.adeo88.groupme.api;

import org.json.JSONArray;
import org.json.JSONObject;

public class Member {

	public String user_id;
	public String nickname;
	public boolean muted;
	public String image_url;
	public String member_id;
	public String[] roles;

	public static Member[] interpretMembers(JSONArray membersJSON) {
		Member[] members = new Member[membersJSON.length()];
		for (int i = 0; i < membersJSON.length(); i++) {
			members[i] = new Member(membersJSON.getJSONObject(i));
		}
		return members;
	}

	public Member(JSONObject json) {
		user_id = json.getString("user_id");
		nickname = json.getString("nickname");
		muted = json.getBoolean("muted");
		image_url = Utils.jsonReadString(json, "image_url");
		member_id = Utils.jsonReadString(json, "id");

		// ,"roles":["admin","owner"]

		if (json.has("roles") && json.get("roles") != JSONObject.NULL) {
			JSONArray rolesJSON = json.getJSONArray("roles");
			roles = new String[rolesJSON.length()];
			for (int i = 0; i < rolesJSON.length(); i++) {
				roles[i] = rolesJSON.getString(i);
			}
		}
	}

	@Override
	public String toString() {
		return "Member [user_id=" + user_id + ", nickname=" + nickname + ", member_id=" + member_id + "]";
	}

}
