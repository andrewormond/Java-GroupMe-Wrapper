package github.adeo88.groupme.api.polling;

import org.json.JSONObject;

import github.adeo88.groupme.api.Group;
import github.adeo88.groupme.api.GroupMeAPI;
import github.adeo88.groupme.api.GroupMeException;

public abstract class UserListener implements ChannelListener {
	protected final GroupMeAPI api;
	@Override
	public void onChannelData(JSONObject data) throws GroupMeException {
		String type = data.getJSONObject("data").getString("type");
		onEvent(new PollEvent(type, data));
	}
	
	public UserListener(GroupMeAPI api) throws GroupMeException {
		this.api = api;
		//api.pushUserSubscribe();
		api.registerListener(this);
	}

	@Override
	public String getChannelName() {
		return "/user/" + api.me.user_id;
	}
	
	public abstract void onEvent(PollEvent event) throws GroupMeException;
}
