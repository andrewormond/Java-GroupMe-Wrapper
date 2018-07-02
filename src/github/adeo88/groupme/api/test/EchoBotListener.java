package github.adeo88.groupme.api.test;

import github.adeo88.groupme.api.Message;
import github.adeo88.groupme.bots.BotListener;

public class EchoBotListener implements BotListener {

	public EchoBotListener() {
	}

	@Override
	public void onMessage(int handlerID, Message message) {
		System.out.printf("[%d]: %s", handlerID, message.toString());
	}

}
