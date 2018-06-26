package github.adeo88.groupme.api;

import org.json.JSONArray;
import org.json.JSONObject;

public class Group {
	public String id;
	public String name;
	public String type;
	public String description;
	public String image_url;
	public String creator_user_id;
	public long created_at;
	public long updated_at;
	public Member[] members;
	public String share_url;
	// public Message[] messages; //TODO: Implement Messages

	public Group(JSONObject json) {
		id = json.getString("id");
		name = json.getString("name");
		type = json.getString("type");
		description = json.getString("description");
		// image_url = json.getString("image_url");
		if (json.has("image_url") && json.get("image_url") != org.json.JSONObject.NULL) {
			image_url = json.getString("image_url");
		}
		creator_user_id = json.getString("creator_user_id");
		created_at = json.getLong("created_at");
		updated_at = json.getLong("updated_at");
		// TODO: Set members
		JSONArray membersJSON = json.getJSONArray("members");
		members = new Member[membersJSON.length()];
		for (int i = 0; i < membersJSON.length(); i++) {
			members[i] = new Member(membersJSON.getJSONObject(0));
		}

		if (json.has("share_url") && json.get("share_url") != org.json.JSONObject.NULL) {
			share_url = json.getString("share_url");
		}
	}

	public static Group[] index(GroupMeAPI api) throws GroupMeException {
		JSONArray groupsJSON = api.sendGetRequest("https://api.groupme.com/v3/groups", true).getJSONArray("response");
		Group[] list = new Group[groupsJSON.length()];
		for (int i = 0; i < groupsJSON.length(); i++) {
			JSONObject groupObj = groupsJSON.getJSONObject(i);
			list[i] = new Group(groupObj);
		}
		return list;

	}

	public static Group[] former(GroupMeAPI api) throws GroupMeException {
		JSONArray groupsJSON = api.sendGetRequest("https://api.groupme.com/v3/groups/former", true)
				.getJSONArray("response");
		Group[] list = new Group[groupsJSON.length()];
		for (int i = 0; i < groupsJSON.length(); i++) {
			JSONObject groupObj = groupsJSON.getJSONObject(i);
			list[i] = new Group(groupObj);
		}
		return list;

	}

	public static Group show(String groupID, GroupMeAPI api) throws GroupMeException {
		JSONObject json = api.sendGetRequest("https://api.groupme.com/v3/groups/" + groupID, true)
				.getJSONObject("response");
		return new Group(json);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Group [id = " + id + ", name = \"" + name + "\"]";
	}

}
