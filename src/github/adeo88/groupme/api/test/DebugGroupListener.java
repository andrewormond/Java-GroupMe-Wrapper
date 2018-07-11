package github.adeo88.groupme.api.test;

import github.adeo88.groupme.api.Group;
import github.adeo88.groupme.api.GroupMeAPI;
import github.adeo88.groupme.api.GroupMeException;
import github.adeo88.groupme.api.polling.GroupListener;
import github.adeo88.groupme.api.polling.PollEvent;

public class DebugGroupListener extends GroupListener {

	public DebugGroupListener(Group group, GroupMeAPI api) throws GroupMeException {
		super(group, api);
	}

	@Override
	public void onEvent(PollEvent event) throws GroupMeException {
		switch(event.type) {
		case favorite:
			System.out.printf("[GroupEvent] %s liked %s\n", event.user_id, event.msg);
			break;
		case typing:
			System.out.printf("[GroupEvent] %s started typing in %s at %s\n", event.user_id, Group.show(event.group_id, api).name, event.time);
			break;
		default:
			System.out.println(event);
			break;
		
		}
		
	}

	

}
