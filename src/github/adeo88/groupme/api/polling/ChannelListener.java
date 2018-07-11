package github.adeo88.groupme.api.polling;

import org.json.JSONObject;

import github.adeo88.groupme.api.GroupMeException;

public interface ChannelListener {
	public String getChannelName();

	public void onChannelData(JSONObject data) throws GroupMeException;

}
