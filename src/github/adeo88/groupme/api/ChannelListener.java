package github.adeo88.groupme.api;

import org.json.JSONObject;

public interface ChannelListener {
	public String getChannelName();

	public void onChannelData(JSONObject data) throws GroupMeException;

}
