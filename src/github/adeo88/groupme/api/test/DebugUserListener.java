package github.adeo88.groupme.api.test;

import org.json.JSONObject;

import github.adeo88.groupme.api.Group;
import github.adeo88.groupme.api.GroupMeAPI;
import github.adeo88.groupme.api.GroupMeException;
import github.adeo88.groupme.api.Message;
import github.adeo88.groupme.api.polling.ChannelListener;
import github.adeo88.groupme.api.polling.PollEvent;
import github.adeo88.groupme.api.polling.UserListener;

public class DebugUserListener extends UserListener {

	public DebugUserListener(GroupMeAPI api) throws GroupMeException {
		super(api);
	}

	@Override
	public void onEvent(PollEvent event) throws GroupMeException {
		switch (event.type) {
		case create:
			System.out.printf("%s said \"%s\" in \"%s\"\n", event.msg.name, event.msg.text,
					Group.show(event.msg.group_id, api).name);
			break;
		default:
			System.out.printf("[User Message {%s}]\n", event.payload.getJSONObject("data").getString("type"));
			for (String key : event.payload.keySet()) {
				System.out.printf("%s : %s\n", key, event.payload.get(key).toString());
			}
			break;

		}

	}

}
