package github.adeo88.groupme.api.test;

import java.util.Optional;

import github.adeo88.groupme.api.Bot;
import github.adeo88.groupme.api.GroupMeException;
import github.adeo88.groupme.api.Message;
import github.adeo88.groupme.bots.BotListener;

public class EchoBotListener implements BotListener {

	Bot bot;
	
	public EchoBotListener() {
	}

	@Override
	public void onMessage(int handlerID, Message message) {
		System.out.printf("[%d]: Recieved %s", handlerID, message.toString());
		String response = message.name + " said: \"" + message.text + "\"";
		System.out.printf("[%d]: Sending %s", handlerID, response);
		try {
			bot.postMessage(response, Optional.empty());
		} catch (GroupMeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setBot(Bot bot) {
		this.bot = bot;
		
	}

	@Override
	public Bot getBot() {
		return bot;
	}

}
