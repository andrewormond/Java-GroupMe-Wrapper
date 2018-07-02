package github.adeo88.groupme.bots;

import github.adeo88.groupme.api.Message;

public interface BotListener {

	public void onMessage(int handlerID, Message message);
}
