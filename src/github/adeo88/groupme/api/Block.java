package github.adeo88.groupme.api;

import java.util.HashMap;

import org.json.JSONObject;

public class Block {
	public String user_id;
	public String blocked_user_id;
	public long created_at = -1;

	private Block(JSONObject json) {
		System.out.println(json);
		user_id = json.getString("user_id");
		this.blocked_user_id = json.getString("blocked_user_id");

		if (json.has("created_at")) {
			this.created_at = json.getLong("created_at");
		}
	}

	public static Block createBlock(String otherUser, GroupMeAPI api) throws GroupMeException {
		HashMap<String, String> parameters = new HashMap<>();
		parameters.put("otherUser", otherUser);
		parameters.put("user", User.Me(api).user_id);
		JSONObject response = api.sendPostRequest("/blocks", parameters, "", true);

		return new Block(response.getJSONObject("response").getJSONObject("block"));
	}

	public int unblock(GroupMeAPI api) throws GroupMeException {
		return Block.unblock(this.blocked_user_id, this.user_id, api);
	}

	public static int unblock(String otherUser, GroupMeAPI api) throws GroupMeException {
		return unblock(otherUser, User.Me(api).user_id, api);
	}

	public static int unblock(String otherUser, String user_id, GroupMeAPI api) throws GroupMeException {
		HashMap<String, String> parameters = new HashMap<>();
		parameters.put("otherUser", otherUser);
		parameters.put("user", user_id);
		return Utils.responseToCode(api.sendPostRequest("/blocks/delete", parameters, "", true));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Block [user_id=" + user_id + ", blocked_user_id=" + blocked_user_id + ", created_at=" + created_at
				+ "]";
	}

	public static boolean isBlocked(String otherUser, GroupMeAPI api) throws GroupMeException {
		return blockBetween(otherUser, User.Me(api).user_id, api);
	}

	public static boolean blockBetween(String otherUser, String user_id, GroupMeAPI api) throws GroupMeException {
		HashMap<String, String> parameters = new HashMap<>();
		parameters.put("otherUser", otherUser);
		parameters.put("user", user_id);
		return api.sendGetRequest("/blocks/between", parameters, true).getJSONObject("response").getBoolean("between");
	}

}
