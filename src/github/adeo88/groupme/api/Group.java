package github.adeo88.groupme.api;

import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

public class Group {
	public String group_id;
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

	public void setMembers(JSONArray membersJSON) {
		members = Member.interpretMembers(membersJSON);
	}

	public Group(JSONObject json) {
		group_id = json.getString("id");
		name = json.getString("name");
		type = json.getString("type");
		description = json.getString("description");

		if (json.has("image_url") && json.get("image_url") != org.json.JSONObject.NULL) {
			image_url = json.getString("image_url");
		}

		creator_user_id = json.getString("creator_user_id");
		created_at = json.getLong("created_at");
		updated_at = json.getLong("updated_at");

		setMembers(json.getJSONArray("members"));

		if (json.has("share_url") && json.get("share_url") != org.json.JSONObject.NULL) {
			share_url = json.getString("share_url");
		}
	}

	public static Group[] index(GroupMeAPI api) throws GroupMeException {
		JSONArray groupsJSON = api.sendGetRequest("/groups", true).getJSONArray("response");
		Group[] list = new Group[groupsJSON.length()];
		for (int i = 0; i < groupsJSON.length(); i++) {
			JSONObject groupObj = groupsJSON.getJSONObject(i);
			list[i] = new Group(groupObj);
		}
		return list;

	}

	public static Group[] former(GroupMeAPI api) throws GroupMeException {
		JSONArray groupsJSON = api.sendGetRequest("/former", true).getJSONArray("response");
		Group[] list = new Group[groupsJSON.length()];
		for (int i = 0; i < groupsJSON.length(); i++) {
			JSONObject groupObj = groupsJSON.getJSONObject(i);
			list[i] = new Group(groupObj);
		}
		return list;

	}

	public static Group show(String groupID, GroupMeAPI api) throws GroupMeException {
		JSONObject json = api.sendGetRequest("/groups/" + groupID, true).getJSONObject("response");
		return new Group(json);
	}

	public void refreshMembers(GroupMeAPI api) throws GroupMeException {
		JSONObject json = api.sendGetRequest("/groups/" + group_id, true).getJSONObject("response");
		setMembers(json.getJSONArray("members"));
	}

	public Member getMember(String user_id) {
		for (Member member : this.members) {
			if (member.user_id.equals(user_id)) {
				return member;
			}
		}
		return null;
	}

	public int removeMember(String user_id, GroupMeAPI api) throws GroupMeException {
		refreshMembers(api);
		Member user = getMember(user_id);
		if (user == null) {
			throw new GroupMeException("No user found for user_id: " + user_id);
		} else if (user.member_id == null) {
			throw new GroupMeException("User found for user_id: " + user_id + ", but no member_id found");
		}

		JSONObject body = new JSONObject();
		body.put("membership_id", user.member_id);

		return api.sendPostRequest("/groups/" + this.group_id + "/members/" + user.member_id + "/remove", "", true);
	}

	public int addMember(JSONObject payload, String guid, GroupMeAPI api) throws GroupMeException {
		JSONArray list = new JSONArray();
		if (guid != null) {
			payload.put("guid", guid);
		}
		list.put(payload);
		JSONObject wrapper = new JSONObject();
		wrapper.put("members", list);
		System.out.println(wrapper.toString());

		int resp = api.sendPostRequest("/groups/" + this.group_id + "/members/add", wrapper.toString(), true);
		return resp;
	}

	private final Pattern emailPattern = Pattern.compile(".*\\@.*\\..*");

	public int addMember(String nickname, String user_id_or_email, String guid, GroupMeAPI api)
			throws GroupMeException {
		JSONObject userObj = new JSONObject();
		userObj.put("nickname", nickname);
		if (emailPattern.matcher(user_id_or_email).matches()) {
			userObj.put("email", user_id_or_email);
		} else {
			userObj.put("user_id", user_id_or_email);
		}
		return addMember(userObj, guid, api);
	}

	public int addMember(String nickname, String user_id_or_email, GroupMeAPI api) throws GroupMeException {
		return addMember(nickname, user_id_or_email, null, api);
	}

	public int addMember(String nickname, long phone_number, String guid, GroupMeAPI api) throws GroupMeException {
		JSONObject userObj = new JSONObject();
		userObj.put("nickname", nickname);
		userObj.put("phone_number", "" + phone_number);
		return addMember(userObj, guid, api);
	}

	public int addMember(String nickname, long phone_number, GroupMeAPI api) throws GroupMeException {
		return addMember(nickname, phone_number, null, api);
	}

	public int updateNickname(String new_nickname, GroupMeAPI api) throws GroupMeException {
		JSONObject memberJSON = new JSONObject();
		memberJSON.put("nickname", new_nickname);
		JSONObject json = new JSONObject();
		json.put("membership", memberJSON);

		return api.sendPostRequest("/groups/" + this.group_id + "/memberships/update", json.toString(), true);

	}

	public Member[] getResults(String resultID, GroupMeAPI api) throws GroupMeException {
		JSONArray membersJSON = api.sendGetRequest("/groups/"+this.group_id+"/members/results/"+resultID, true).getJSONArray("members");
		return Member.interpretMembers(membersJSON);
	}

	@Override
	public String toString() {
		return "Group [group_id = " + group_id + ", name = \"" + name + "\"]";
	}

}
