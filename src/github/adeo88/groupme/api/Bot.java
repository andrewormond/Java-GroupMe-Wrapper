package github.adeo88.groupme.api;

import java.util.HashMap;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

public class Bot {

	public String bot_id;
	public String group_id;
	public String name;
	public String avatar_url;
	public String callback_url;
	public boolean dm_notification;

	private Bot(JSONObject json) {
		this.bot_id = json.getString("bot_id");
		this.group_id = json.getString("group_id");
		this.name = json.getString("name");
		this.avatar_url = Utils.jsonReadString(json, "avatar_url");
		this.callback_url = Utils.jsonReadString(json, "callback_url");
		this.dm_notification = json.getBoolean("dm_notification");
	}

	public static Bot create(String name, String group_id, Optional<String> avatar_url, Optional<String> callback_url,
			Optional<Boolean> dm_notification, GroupMeAPI api) throws GroupMeException {
		JSONObject payload = new JSONObject();
		JSONObject json = new JSONObject();
		json.put("name", name);
		json.put("group_id", group_id);
		if (avatar_url.isPresent()) {
			json.put("avatar_url", avatar_url.get());
		}
		if (callback_url.isPresent()) {
			json.put("callback_url", callback_url.get());
		}
		if (dm_notification.isPresent()) {
			json.put("dm_notification", dm_notification.get());
		}
		payload.put("bot", json);
		JSONObject response = api.sendPostRequest("/bots", payload.toString(), true);
		System.out.println("response: " + response);
		return new Bot(response.getJSONObject("response").getJSONObject("bot"));
	}

	public int postMessage(String text, Optional<String> picture_url, GroupMeAPI api) throws GroupMeException {
		JSONObject payload = new JSONObject();
		payload.put("bot_id", this.bot_id);
		payload.put("text", text);
		if (picture_url.isPresent()) {
			String url = picture_url.get();
			if (url.startsWith("https://i.groupme.com/")) {
				payload.put("picture_url", url);
			} else {
				throw new GroupMeException("Picture URL needs to be uploaded through the image API (" + url + ")", 400);
			}
		}

		return Utils.responseToCode(api.sendPostRequest("/bots/post", payload.toString(), false));

	}

	public static Bot[] interpretBots(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new Bot[0];
		}
		Bot[] bots = new Bot[jsonArray.length()];
		for (int i = 0; i < jsonArray.length(); i++) {
			bots[i] = new Bot(jsonArray.getJSONObject(i));
		}
		return bots;
	}

	public static Bot get(String bot_id, GroupMeAPI api) throws GroupMeException {
		Bot[] bots = Bot.index(api);
		for (Bot bot : bots) {
			if (bot.bot_id.equals(bot_id)) {
				return bot;
			}
		}
		return null;
	}

	public static Bot[] index(GroupMeAPI api) throws GroupMeException {
		JSONObject response = api.sendGetRequest("/bots", true);
		System.out.println(response);
		return Bot.interpretBots(response.getJSONArray("response"));
	}

	public int destroy(GroupMeAPI api) throws GroupMeException {
		HashMap<String, String> parameters = new HashMap<>();
		parameters.put("bot_id", this.bot_id);
		return Utils.responseToCode(api.sendPostRequest("/bots/destroy", parameters, "", true));
	}

	@Override
	public String toString() {
		return "Bot [bot_id=" + bot_id + ", group_id=" + group_id + ", name=" + name + ", avatar_url=" + avatar_url
				+ ", callback_url=" + callback_url + ", dm_notification=" + dm_notification + "]";
	}

}
