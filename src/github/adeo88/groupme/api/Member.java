package github.adeo88.groupme.api;

import org.json.JSONObject;

public class Member {
	
	public String user_id;
	public String nickname;
	public boolean muted;
	public String image_url;

	public Member(JSONObject json) {
		user_id = json.getString("user_id");
		nickname = json.getString("nickname");
		muted = json.getBoolean("muted");
		if(json.has("image_url") && json.get("image_url") != JSONObject.NULL){
			image_url = json.getString("image_url");
		}
	}

	@Override
	public String toString() {
		return "Member [user_id=" + user_id + ", nickname=" + nickname + "]";
	}
	
	

}
