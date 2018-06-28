package github.adeo88.groupme.api;

import java.io.PrintStream;
import java.util.HashMap;
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
	String last_message_id;
	String first_message_id;
	long last_message_created_at;
	public HashMap<String, Message> messages = new HashMap<>();

	public void setMembers(JSONArray membersJSON) {
		members = Member.interpretMembers(membersJSON);
	}

	public void setMessages(JSONObject wrapper) {
		int count = wrapper.getInt("count");
		String message_id = wrapper.getString("last_message_id");
		if (first_message_id == null) {
			first_message_id = message_id;
		}
		if (this.last_message_id == null) {
			this.last_message_id = first_message_id;
		}
		long message_time = wrapper.getLong("last_message_created_at");
		if (message_time > this.last_message_created_at) {
			last_message_id = message_id;
		}
	}

	public void dumpMessages(PrintStream ps) {
		for (String key : this.messages.keySet()) {
			ps.println(messages.get(key));
		}
	}

	private Group(JSONObject json) {
		group_id = json.getString("id");
		name = json.getString("name");
		type = json.getString("type");
		description = Utils.jsonReadString(json, "description");
		image_url = Utils.jsonReadString(json, "image_url");

		creator_user_id = json.getString("creator_user_id");
		created_at = json.getLong("created_at");
		updated_at = json.getLong("updated_at");

		setMembers(json.getJSONArray("members"));

		share_url = Utils.jsonReadString(json, "share_url");
	}

	public static Group[] indexGroups(GroupMeAPI api) throws GroupMeException {
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

	public void refresh(GroupMeAPI api) throws GroupMeException {
		JSONObject json = api.sendGetRequest("/groups/" + group_id, true).getJSONObject("response");
		setMembers(json.getJSONArray("members"));
		setMessages(json.getJSONObject("messages"));
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
		refresh(api);
		Member user = getMember(user_id);
		if (user == null) {
			throw new GroupMeException("No user found for user_id: " + user_id, 404);
		} else if (user.member_id == null) {
			throw new GroupMeException("User found for user_id: " + user_id + ", but no member_id found", 404);
		}

		JSONObject body = new JSONObject();
		body.put("membership_id", user.member_id);

		return Utils.responseToCode(
				api.sendPostRequest("/groups/" + this.group_id + "/members/" + user.member_id + "/remove", "", true));
	}

	public String addMember(JSONObject payload, String guid, GroupMeAPI api) throws GroupMeException {
		JSONArray list = new JSONArray();
		if (guid != null) {
			payload.put("guid", guid);
		}
		list.put(payload);
		JSONObject wrapper = new JSONObject();
		wrapper.put("members", list);

		JSONObject response = Utils.jsonReadJSOBObject(
				api.sendPostRequest("/groups/" + this.group_id + "/members/add", wrapper.toString(), true), "response");
		return Utils.jsonReadString(response, "results_id");
	}

	private final Pattern emailPattern = Pattern.compile(".*\\@.*\\..*");

	public String addMember(String nickname, String user_id_or_email, String guid, GroupMeAPI api)
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

	public String addMember(String nickname, String user_id_or_email, GroupMeAPI api) throws GroupMeException {
		return addMember(nickname, user_id_or_email, null, api);
	}

	public String addMember(String nickname, long phone_number, String guid, GroupMeAPI api) throws GroupMeException {
		JSONObject userObj = new JSONObject();
		userObj.put("nickname", nickname);
		userObj.put("phone_number", "" + phone_number);
		return addMember(userObj, guid, api);
	}

	public String addMember(String nickname, long phone_number, GroupMeAPI api) throws GroupMeException {
		return addMember(nickname, phone_number, null, api);
	}

	public int updateNickname(String new_nickname, GroupMeAPI api) throws GroupMeException {
		JSONObject memberJSON = new JSONObject();
		memberJSON.put("nickname", new_nickname);
		JSONObject json = new JSONObject();
		json.put("membership", memberJSON);

		return Utils.responseToCode(
				api.sendPostRequest("/groups/" + this.group_id + "/memberships/update", json.toString(), true));

	}

	public Member[] getResults(String resultID, GroupMeAPI api) throws GroupMeException {
		try {
			JSONArray membersJSON = api
					.sendGetRequest("/groups/" + this.group_id + "/members/results/" + resultID, true)
					.getJSONObject("response").getJSONArray("members");
			return Member.interpretMembers(membersJSON);
		} catch (GroupMeException e) {
			if (e.code == 503) {
				throw new GroupMeException("Results are not ready yet, try again in a moment", e.code);
			} else if (e.code == 404) {
				throw new GroupMeException("Results have expired or id is invalid", e.code);
			}
		}
		return null;
	}

	// TODO: Implement Parameters
	public Message[] indexMessages(GroupMeAPI api) throws GroupMeException {
		JSONObject jsonPackage = api.sendGetRequest("/groups/" + this.group_id + "/messages", true)
				.getJSONObject("response");
		Message[] messages = Message.interpretMessages(jsonPackage.getJSONArray("messages"));

		return messages;
	}

	@Override
	public String toString() {
		return "Group [group_id = " + group_id + ", name = \"" + name + "\"]";
	}

	public static Group create(String name, String description, String image_url, boolean share, GroupMeAPI api)
			throws GroupMeException {
		if (name == null) {
			throw new GroupMeException("Name is required", 400);
		}
		JSONObject payload = new JSONObject();
		payload.put("name", name);
		if (description != null) {
			payload.put("description", description);
		}
		if (image_url != null) {
			payload.put("image_url", image_url);
		}
		if(share) {
			payload.put("share", true);
		}
		JSONObject response = api.sendPostRequest("/groups", payload.toString(), true);
		//Group Create Response: {"meta":{"code":201},"response":{"creator_user_id":"20623362","max_memberships":500,"image_url":null,"description":null,"created_at":1530212666,"type":"private","share_qr_code_url":null,"updated_at":1530212666,"group_id":"41753289","share_url":null,"members":[{"user_id":"20623362","image_url":"http://i.groupme.com/748x750.jpeg.f45ba9bb6ee745d3a678240e8dc94ec3","autokicked":false,"roles":["admin","owner"],"nickname":"Andrew Ormond","id":"349788274","muted":false}],"name":"Test: 641","max_members":500,"messages":{"preview":{"attachments":[],"image_url":null,"nickname":null,"text":null},"last_message_id":null,"count":0,"last_message_created_at":null},"phone_number":"+1 3182667490","office_mode":false,"id":"41753289"}}
		if(Utils.responseToCode(response) == 201) {
			return new Group(response.getJSONObject("response"));
		}
		return null;
	}
	
	public static int destroy(String groupID, GroupMeAPI api) throws GroupMeException {
		return Utils.responseToCode(api.sendPostRequest("/groups/"+groupID+"/destroy", null, true));
	}

}
