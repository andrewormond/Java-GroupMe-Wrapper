package github.adeo88.groupme.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import de.roderick.weberknecht.WebSocket;
import de.roderick.weberknecht.WebSocketEventHandler;
import de.roderick.weberknecht.WebSocketMessage;
import github.adeo88.groupme.api.polling.ChannelListener;

public class GroupMeAPI implements ChannelListener, WebSocketEventHandler {

	String token;
	public boolean debugEnabled = false;
	public String clientID;
	private int pushID = 0;
	public User me;

	private WebSocket webSocket;
	private HashMap<String, ChannelListener> channels = new HashMap<>();
	private volatile boolean polling = false;

	public void setToken(String token) {
		this.token = token;
	}

	public void registerListener(ChannelListener listener) {
		channels.put(listener.getChannelName(), listener);
	}

	public void unregisterListener(ChannelListener listener) {
		if (channels.containsKey(listener.getChannelName())) {
			channels.remove(listener.getChannelName());
		}
	}

	public GroupMeAPI(String token) {
		setToken(token);
		this.registerListener(this);
	}

	@Override
	public String toString() {
		return "GroupMeAPI";
	}

	public JSONObject sendGetRequest(String url, boolean authenticate) throws GroupMeException {
		return sendGetRequest(url, null, new HashMap<>(), authenticate);
	}

	public JSONObject sendGetRequest(String url, String body, boolean authenticate) throws GroupMeException {
		return sendGetRequest(url, body, new HashMap<>(), authenticate);
	}

	public JSONObject sendGetRequest(String url, HashMap<String, String> parameters, boolean authenticate)
			throws GroupMeException {
		return sendGetRequest(url, null, parameters, authenticate);
	}

	public JSONObject sendGetRequest(String url, String body, HashMap<String, String> parameters, boolean authenticate)
			throws GroupMeException {
		url = "https://api.groupme.com/v3" + url;
		if (authenticate) {
			parameters.put("token", token);
		}
		return Utils.sendGetRequest(url, body, parameters);
	}

	public JSONObject sendPostRequest(String url, String body, boolean authenticate) throws GroupMeException {
		return this.sendPostRequest(url, new HashMap<>(), body, authenticate);
	}

	public JSONObject sendPostRequest(String url, HashMap<String, String> parameters, String body, boolean authenticate)
			throws GroupMeException {
		url = "https://api.groupme.com/v3" + url;
		if (authenticate) {
			parameters.put("token", token);
		}
		return Utils.sendPostRequest(url, parameters, body);
	}


	private String recievedMessage = null;
	public void pushApiHandshake() throws GroupMeException {
		URI url = null;
		try {
			url = new URI("ws://push.groupme.com/faye");
		} catch (URISyntaxException e) {
			throw new GroupMeException(e.getMessage(), 500);
		}
		webSocket = new WebSocket(url);

		JSONObject handshake = new JSONObject();
		handshake.put("channel", "/meta/handshake");
		handshake.put("version", 1.0d);
		handshake.put("supportedConnectionTypes", new JSONArray().put("long-polling"));
		handshake.put("id", ++this.pushID);
		JSONArray payload = new JSONArray();
		payload.put(handshake);
		this.recievedMessage = null;
		webSocket.setEventHandler(this);

		webSocket.connect();

		webSocket.send(payload.toString());
		
		final int checkTime = 10;
		final int totalTime = 5*1000;
		int timeAccumulated = 0;
		
		while(recievedMessage == null && timeAccumulated < totalTime) {
			timeAccumulated += checkTime;
			try {
				Thread.sleep(checkTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
				recievedMessage = null;
				return;
			}
		}
		
		if(recievedMessage != null) {
			System.out.println("Recieved Message: "+recievedMessage);
		}else {
			throw new GroupMeException("Could not connect to server", 500);
		}
		recievedMessage = null;
		

		/*
		JSONObject response = Utils.sendPostRequest("https://push.groupme.com/faye", null, payload.toString());
		System.out.printf("Response: %s\n", response);
		this.clientID = response.getString("clientId");
		if (!response.getBoolean("successful")) {
			throw new GroupMeException("Push API Handshake failed", 500);
		}*/
	}

	public void pushUserSubscribe() throws GroupMeException {
		if (clientID == null) {
			throw new GroupMeException("No clientId found. pushApiHandshake must successfully complete first.", 400);
		}

		JSONObject payload = new JSONObject();
		payload.put("channel", "/meta/subscribe");
		payload.put("clientId", this.clientID);
		payload.put("subscription", "/user/" + User.Me(this).user_id);
		payload.put("id", ++this.pushID);

		JSONObject ext = new JSONObject();
		ext.put("access_token", this.token);
		ext.put("timestamp", new Date().getTime());
		payload.put("ext", ext);

		JSONArray body = new JSONArray();
		body.put(payload);
		JSONObject response = Utils.sendPostRequest("https://push.groupme.com/faye", null, body.toString());
		if (!response.getBoolean("successful")) {
			throw new GroupMeException("Push API failed: \"" + response.getString("error") + "\"", 500);
		}
	}

	/*
	 * public void pushGroupSubscribe(Group group) throws GroupMeException { if
	 * (clientID == null) { throw new
	 * GroupMeException("No clientId found. pushApiHandshake must successfully complete first."
	 * , 400); }
	 * 
	 * JSONObject payload = new JSONObject(); payload.put("channel",
	 * "/meta/subscribe"); payload.put("clientId", this.clientID);
	 * payload.put("subscription", "/group/" + group.group_id); payload.put("id",
	 * ++this.pushID);
	 * 
	 * JSONObject ext = new JSONObject(); ext.put("access_token", this.token);
	 * ext.put("timestamp", new Date().getTime()); payload.put("ext", ext);
	 * 
	 * JSONArray body = new JSONArray(); body.put(payload);
	 * System.out.println(body); JSONObject response =
	 * Utils.sendPostRequest("https://push.groupme.com/faye", null,
	 * body.toString()); System.out.println(response); if
	 * (!response.getBoolean("successful")) { throw new
	 * GroupMeException("Push API failed: \"" + response.getString("error") + "\"",
	 * 500); } }
	 * 
	 * public void longPollData() throws GroupMeException { if (clientID == null) {
	 * throw new
	 * GroupMeException("No clientId found. pushApiHandshake must successfully complete first."
	 * , 400); }
	 * 
	 * JSONObject payload = new JSONObject(); payload.put("channel",
	 * "/meta/connect"); payload.put("clientId", this.clientID);
	 * payload.put("connectionType", "websocket"); payload.put("id", ++this.pushID);
	 * JSONArray body = new JSONArray(); body.put(payload); JSONArray response =
	 * Utils.sendPostRequestToArray("https://push.groupme.com/faye", null,
	 * body.toString()); for (int i = 0; i < response.length(); i++) { JSONObject
	 * data = response.getJSONObject(i); ChannelListener listener =
	 * this.channels.get(data.getString("channel")); if (listener != null) {
	 * listener.onChannelData(data);
	 * 
	 * } else { System.out.printf("Unkown: [%s] : ", data.getString("channel"));
	 * System.out.println(data); } } }
	 */

	@Override
	public String getChannelName() {
		return "/meta/connect";
	}

	@Override
	public void onChannelData(JSONObject data) throws GroupMeException {
		if (!data.getBoolean("successful")) {
			throw new GroupMeException("Push API failed: \"" + data.getString("error") + "\"", 500);
		}
	}

	@Override
	public void onClose() {
		webSocket = null;
		
	}

	@Override
	public void onError(IOException arg0) {}

	@Override
	public void onMessage(WebSocketMessage arg0) {}

	@Override
	public void onOpen() {}

	@Override
	public void onPing() {}

	@Override
	public void onPong() {}

	/*
	 * public void startPolling() { this.polling = true; final GroupMeAPI thisAPI =
	 * this; Thread t = new Thread(new Runnable() {
	 * 
	 * @Override public void run() { final GroupMeAPI api = thisAPI; while
	 * (api.polling) { try { try { api.pollData(); }catch(Exception e){
	 * System.err.println(e.getMessage()); } Thread.sleep(api.pollRate); } catch
	 * (InterruptedException e) { e.printStackTrace(); return; } }
	 * 
	 * }
	 * 
	 * }); t.start(); }
	 * 
	 * public void stopPolling() { this.polling = false; }
	 */

}
