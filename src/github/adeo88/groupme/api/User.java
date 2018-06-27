package github.adeo88.groupme.api;

import org.json.JSONObject;

public class User {
	public String user_id;
	public String phone_number;
	public String image_url;
	public long created_at;
	public long updated_at;
	public String email;
	public boolean sms;

	private User(JSONObject json) {

		user_id = GroupMeAPI.ReadJSONStringWithNull(json, "id");
		phone_number = GroupMeAPI.ReadJSONStringWithNull(json, "phone_number");
		image_url = GroupMeAPI.ReadJSONStringWithNull(json, "image_url");
		created_at = json.getLong("created_at");
		updated_at = json.getLong("updated_at");
		email = GroupMeAPI.ReadJSONStringWithNull(json, "email");
		sms = json.getBoolean("sms");
	}

	public static User Me(GroupMeAPI api) throws GroupMeException {
		JSONObject json = api.sendGetRequest("/users/me", true).getJSONObject("response");
		return new User(json);
	}

	@Override
	public String toString() {
		return "User [user_id=" + user_id + ", phone_number=" + phone_number + ", image_url=" + image_url
				+ ", created_at=" + created_at + ", updated_at=" + updated_at + ", email=" + email + ", sms=" + sms
				+ "]";
	}

	public static int update(String avatar_url, String name, String email, String zip_code, GroupMeAPI api)
			throws GroupMeException {
		JSONObject payload = new JSONObject();

		if (avatar_url != null) {
			payload.put("avatar_url", avatar_url);
		}

		if (name != null) {
			payload.put("name", name);
		}

		if (email != null) {
			payload.put("email", email);
		}

		if (zip_code != null) {
			payload.put("zip_code", zip_code);
		}

		return api.sendPostRequest("/users/update", payload.toString(), true);
	}

}
