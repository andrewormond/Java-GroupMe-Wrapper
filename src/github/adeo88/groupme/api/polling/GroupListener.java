package github.adeo88.groupme.api.polling;

import org.json.JSONObject;

import github.adeo88.groupme.api.Group;
import github.adeo88.groupme.api.GroupMeAPI;
import github.adeo88.groupme.api.GroupMeException;

public abstract class GroupListener implements ChannelListener {
	public Group group;
	protected final GroupMeAPI api;
	@Override
	public void onChannelData(JSONObject data) throws GroupMeException {
		String type = data.getJSONObject("data").getString("type");
		onEvent(new PollEvent(type, data));
	}
	
	public GroupListener(Group group, GroupMeAPI api) throws GroupMeException {
		this.api = api;
		this.group = group;
		api.pushGroupSubscribe(group);
		api.registerListener(this);
	}

	@Override
	public String getChannelName() {
		return "/group/"+group.group_id;
	}
	
	public abstract void onEvent(PollEvent event) throws GroupMeException;
}
