package github.adeo88.groupme.api;

import java.util.Optional;

import org.json.JSONObject;

/**
 * A <code>User</code> contains information about the user on a global scale in
 * the <code>GroupMeAPI</code>.
 * 
 * @author adeo8
 *
 */

public class User {

	/**
	 * The <code>user_id</code> of this <code>User</code>. Should always be present.
	 */
	public String user_id;

	/**
	 * The phone number associated with this <code>User</code>. May be
	 * <code>null</code>.
	 */
	public String phone_number;

	/**
	 * The URL for this <code>User</code>'s Avatar. May be <code>null</code> if user
	 * has not set an Avatar yet.
	 */
	public String image_url;

	/**
	 * Time this <code>User</code> was originally created in the Unix epoch format,
	 * which is the number of seconds since January 1, 1970 in the UTC (GMT)
	 * timezone.
	 */
	public long created_at;

	/**
	 * Time this <code>User</code> was last updated in the Unix epoch format, which
	 * is the number of seconds since January 1, 1970 in the UTC (GMT) timezone.
	 */
	public long updated_at;

	/**
	 * The email address associated with this <code>User</code>. May be
	 * <code>null</code>.
	 */
	public String email;

	/**
	 * Flag indicating whether this <code>User</code> is in sms mode.
	 */
	public boolean sms;

	/**
	 * Constructs the <code>User</code> object from a <code>JSONObject</code>.
	 * 
	 * @param <code>JSONObject</code>
	 *            to interpret.
	 */
	private User(JSONObject json) {

		user_id = Utils.jsonReadString(json, "id");
		phone_number = Utils.jsonReadString(json, "phone_number");
		image_url = Utils.jsonReadString(json, "image_url");
		created_at = json.getLong("created_at");
		updated_at = json.getLong("updated_at");
		email = Utils.jsonReadString(json, "email");
		sms = json.getBoolean("sms");
	}

	/**
	 * Constructs a <code>User</code> associated with the application token in the
	 * <code>GroupMeAPI</code>.
	 * 
	 * @param api
	 *            The <code>GroupMeAPI<code> to use.
	 * @return <code>User</code> corresponding to this api's token.
	 * @throws GroupMeException
	 *             When there is a JSON error or Networking error.
	 */
	public static User Me(GroupMeAPI api) throws GroupMeException {
		JSONObject json = api.sendGetRequest("/users/me", true).getJSONObject("response");
		return new User(json);
	}

	/**
	 * Updates the users account with the new information provided. The avatar_url,
	 * name, email, and zip_code can be set.
	 * 
	 * @param avatar_url
	 *            Optional string in WWW URL format
	 * @param name
	 *            Optional string
	 * @param email
	 *            Optional string in an email format (Ex: tmp@fake.com)
	 * @param zip_code
	 *            Optional string representing the zip code
	 * @param api
	 *            <code>GroupMeAPI</code> containing the token of the required user.
	 * @return HTTP status code returned from the server (200: OK)
	 * @throws GroupMeException
	 */
	public static int update(Optional<String> avatar_url, Optional<String> name, Optional<String> email,
			Optional<String> zip_code, GroupMeAPI api) throws GroupMeException {
		JSONObject payload = new JSONObject();

		if (avatar_url.isPresent()) {
			payload.put("avatar_url", avatar_url.get());
		}

		if (name.isPresent()) {
			payload.put("name", name.get());
		}

		if (email.isPresent()) {
			payload.put("email", email.get());
		}

		if (zip_code.isPresent()) {
			payload.put("zip_code", zip_code.get());
		}

		return Utils.responseToCode(api.sendPostRequest("/users/update", payload.toString(), true));
	}

	/**
	 * Turns this <code>User</code> into a readable string.
	 * 
	 * @return A <code>String</code> representation of this <code>User</code>.
	 */
	@Override
	public String toString() {
		return "User [user_id=" + user_id + ", phone_number=" + phone_number + ", image_url=" + image_url
				+ ", created_at=" + created_at + ", updated_at=" + updated_at + ", email=" + email + ", sms=" + sms
				+ "]";
	}

}
