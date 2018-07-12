package github.adeo88.groupme.api;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A <code>Member</code> represents the GroupMe API Member. A
 * <code>Member</code> belongs to a group and is associated with a specific
 * user_id. The nickname can be different than the account name associated with
 * the user_id. A <code>Member</code> contains a member_id specific to a group
 * of which most calls depend on. Additionally a member can have associated
 * roles assigned to it, such as "owner".
 * 
 * @author adeo8
 *
 */
public class Member {

	/**
	 * The user_id associated with this Member.
	 */
	public String user_id;

	/**
	 * The nickname associated with this Member.
	 */
	public String nickname;

	/**
	 * Flag stating whether this Member has muted themselves. A muted member should
	 * not receive push notifications from the associated group.
	 */
	public boolean muted;

	/**
	 * The URL associated with the Member's avatar. This may be null if the Member
	 * has not set an avatar.
	 */
	public String image_url;

	/**
	 * The unique ID associating this Member to the Group. Note: This is not the
	 * same as the user_id.
	 */
	public String member_id;

	/**
	 * The list of roles this Member is associated with. Roles discovered so far:
	 * ["admin","owner"].
	 */
	public String[] roles;

	/**
	 * Constructs a Member from a JSONObject.
	 * 
	 * @param json
	 *            JSONObject to interpret
	 */
	public Member(JSONObject json) {
		user_id = json.getString("user_id");
		nickname = json.getString("nickname");
		muted = json.getBoolean("muted");
		image_url = Utils.jsonReadString(json, "image_url");
		member_id = Utils.jsonReadString(json, "id");

		if (json.has("roles") && json.get("roles") != JSONObject.NULL) {
			JSONArray rolesJSON = json.getJSONArray("roles");
			roles = new String[rolesJSON.length()];
			for (int i = 0; i < rolesJSON.length(); i++) {
				roles[i] = rolesJSON.getString(i);
			}
		}
	}

	/**
	 * Interprets a JSONArray of members and converts them into a Member Array.
	 * 
	 * @param membersJSON
	 *            The JSONArray to interpret
	 * @return A Member Array
	 */
	public static Member[] interpretMembers(JSONArray membersJSON) {
		if(membersJSON == null) {
			return new Member[0];
		}
		Member[] members = new Member[membersJSON.length()];
		for (int i = 0; i < membersJSON.length(); i++) {
			members[i] = new Member(membersJSON.getJSONObject(i));
		}
		return members;
	}

	/**
	 * Returns a readable string representation of the Member object.
	 * 
	 * @return A readable string representation
	 */
	@Override
	public String toString() {
		return "Member [user_id=" + user_id + ", nickname=" + nickname + ", member_id=" + member_id + "]";
	}

}
