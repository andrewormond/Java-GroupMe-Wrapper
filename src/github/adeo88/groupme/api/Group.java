package github.adeo88.groupme.api;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

public class Group {
	public enum TimePeriod {
		day, week, month
	}

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

	public Group(JSONObject json) {
		group_id = json.getString("id");
		name = json.getString("name");
		type = json.getString("type");
		description = Utils.jsonReadString(json, "description");
		image_url = Utils.jsonReadString(json, "image_url");

		creator_user_id = json.getString("creator_user_id");
		created_at = json.getLong("created_at");
		updated_at = json.getLong("updated_at");

		setMembers(json.optJSONArray("members"));

		share_url = Utils.jsonReadString(json, "share_url");
	}
	
	public static Group[] indexGroups(GroupMeAPI api, Optional<Integer> page, Optional<Integer> per_page, Optional<Boolean> omitMemberships) throws GroupMeException {

		HashMap<String, String> parameters = new HashMap<>();
		if(page.isPresent()) {
			parameters.put("page", page.get().toString());
		}
		if(per_page.isPresent()) {
			parameters.put("per_page", per_page.get().toString());
		}
		if(omitMemberships.isPresent() && omitMemberships.get()) {
			parameters.put("omit", "memberships");
		}
		
		JSONArray groupsJSON = api.sendGetRequest("/groups", parameters, true).getJSONArray("response");
		Group[] list = new Group[groupsJSON.length()];
		for (int i = 0; i < groupsJSON.length(); i++) {
			JSONObject groupObj = groupsJSON.getJSONObject(i);
			list[i] = new Group(groupObj);
		}
		return list;
	}

	public static Group[] indexGroups(GroupMeAPI api) throws GroupMeException {
		return Group.indexGroups(api, Optional.empty(), Optional.empty(), Optional.empty());
	}

	public static Group[] former(GroupMeAPI api) throws GroupMeException {
		JSONArray groupsJSON = api.sendGetRequest("/groups/former", true).getJSONArray("response");
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

	public Message[] indexMessages(GroupMeAPI api) throws GroupMeException {
		return indexMessages(Optional.empty(), Optional.empty(), Optional.empty(), api);
	}

	public Message[] indexMessages(int limit, GroupMeAPI api) throws GroupMeException {
		return indexMessages(Optional.of(limit), Optional.empty(), Optional.empty(), api);
	}

	public Message[] indexMessages(Optional<Integer> limit, Optional<String> before_id, Optional<String> since_id,
			GroupMeAPI api) throws GroupMeException {
		String url = "/groups/" + this.group_id + "/messages";
		HashMap<String, String> parameters = new HashMap<>();
		if (limit.isPresent()) {
			parameters.put("limit", "" + limit.get());
		}
		if (before_id.isPresent()) {
			parameters.put("before_id", before_id.get());
		}
		if (since_id.isPresent()) {
			parameters.put("since_id", since_id.get());
		}
		JSONObject jsonPackage = api.sendGetRequest(url, parameters, true).getJSONObject("response");
		Message[] messages = Message.interpretMessages(jsonPackage.getJSONArray("messages"));

		return messages;
	}

	@Override
	public String toString() {
		return "Group [group_id = " + group_id + ", "+this.members.length+" members, name = \"" + name + "\"]";
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
		if (share) {
			payload.put("share", true);
		}
		JSONObject response = api.sendPostRequest("/groups", payload.toString(), true);
		if (Utils.responseToCode(response) == 201) {
			return new Group(response.getJSONObject("response"));
		}
		return null;
	}

	public int destroy(GroupMeAPI api) throws GroupMeException {
		return Utils.responseToCode(api.sendPostRequest("/groups/" + this.group_id + "/destroy", null, true));
	}

	public Message createMessage(String text, String source_guid, JSONArray attachments, GroupMeAPI api)
			throws GroupMeException {
		JSONObject json = new JSONObject();
		json.put("text", text);
		json.put("source_guid", source_guid);
		if (attachments != null) {
			json.put("attachments", attachments);
		}
		JSONObject payload = new JSONObject();
		payload.put("message", json);
		JSONObject response = api.sendPostRequest("/groups/" + this.group_id + "/messages", payload.toString(), true);
		return new Message(response.getJSONObject("response").getJSONObject("message"));
	}

	public Message[] getLeaderboard(TimePeriod time, GroupMeAPI api) throws GroupMeException {
		HashMap<String, String> parameters = new HashMap<>();
		parameters.put("period", time.toString());

		JSONObject jsonPackage = api.sendGetRequest("/groups/" + this.group_id + "/likes", parameters, true)
				.getJSONObject("response");
		Message[] messages = Message.interpretMessages(jsonPackage.getJSONArray("messages"));

		return messages;
	}

	public Message[] getMyLikes(GroupMeAPI api) throws GroupMeException {
		JSONObject jsonPackage = api.sendGetRequest("/groups/" + this.group_id + "/likes/mine", true)
				.getJSONObject("response");
		Message[] messages = Message.interpretMessages(jsonPackage.getJSONArray("messages"));

		return messages;
	}

	public Message[] getMyHits(GroupMeAPI api) throws GroupMeException {
		JSONObject jsonPackage = api.sendGetRequest("/groups/" + this.group_id + "/likes/for_me", true)
				.getJSONObject("response");
		Message[] messages = Message.interpretMessages(jsonPackage.getJSONArray("messages"));

		return messages;
	}

	private static final Pattern sharePattern = Pattern.compile(".*/(\\w+)/(\\w+)\\b");

	public static Group join(String share_url, GroupMeAPI api) throws GroupMeException {
		Matcher m = sharePattern.matcher(share_url.trim());
		if (m.matches()) {
			String group_id = m.group(1);
			String share_token = m.group(2);
			System.out.println("group: " + group_id + " share_token: " + share_token);
			JSONObject response = api.sendPostRequest("/groups/" + group_id + "/join/" + share_token, "", true);

			return new Group(response.getJSONObject("response").getJSONObject("group"));
		} else {
			throw new GroupMeException("Bad Share URL: " + share_url, 400);
		}
	}

	public Group rejoin(GroupMeAPI api) throws GroupMeException {
		return Group.rejoin(this.group_id, api);
	}

	public static Group rejoin(String group_id, GroupMeAPI api) throws GroupMeException {
		HashMap<String, String> parameters = new HashMap<>();
		parameters.put("group_id", group_id);
		JSONObject response = api.sendPostRequest("/groups/join", parameters, "", true);
		return new Group(response.getJSONObject("response"));
	}

	public void changeOwner(String owner_id, GroupMeAPI api) throws GroupMeException {
		JSONObject jobj = new JSONObject();
		jobj.put("group_id", this.group_id);
		jobj.put("owner_id", owner_id);
		JSONArray requests = new JSONArray();
		requests.put(jobj);
		JSONObject payload = new JSONObject();
		payload.put("requests", requests);
		JSONObject response = api.sendPostRequest("/groups/change_owners", payload.toString(), true);
		System.out.println(response);
		
		int code = response.getJSONObject("response").getJSONArray("results").getJSONObject(0).getInt("status");
		switch (code) {
		case 200:
			return;
		case 400:
			throw new GroupMeException("Already owner", code);
		case 403:
			throw new GroupMeException("Requester not owner", code);
		case 404:
			throw new GroupMeException("group or new owner not found or new owner is not member of the group", code);
		case 405:
			throw new GroupMeException("request object is missing required field or any of the required fields is not an ID", code);
		default:
			throw new GroupMeException("Unkown Code: "+code, code);
		}
	}

}
