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

public class GroupMeAPI implements WebSocketEventHandler {

	String token;
	public boolean debugEnabled = false;
	public String clientID;
	private int pushID = 0;
	public User me;

	private WebSocket webSocket;

	public void setToken(String token) {
		this.token = token;
	}

	public GroupMeAPI(String token) {
		setToken(token);
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
		handshake.put("supportedConnectionTypes", new JSONArray().put("websocket"));
		handshake.put("id", ++this.pushID);
		JSONArray payload = new JSONArray();
		payload.put(handshake);
		this.recievedMessage = null;
		webSocket.setEventHandler(this);

		webSocket.connect();

		webSocket.send(payload.toString());
		
		waitForMessage(5000, 5);
		
		if(recievedMessage != null) {
			JSONObject response = new JSONArray(recievedMessage).getJSONObject(0);
			this.clientID = response.getString("clientId");
			System.out.printf("ClientId: %s\n", this.clientID);
			if (!response.getBoolean("successful")) {
				throw new GroupMeException("Push API Handshake failed", 500);
			}
		}else {
			throw new GroupMeException("Could not connect to server", 500);
		}
		recievedMessage = null;
		
	}

	public void pushUserSubscribe() throws GroupMeException {
		if (clientID == null || webSocket == null) {
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
		
		webSocket.send(body.toString());
		
		waitForMessage(5000, 5);
		
		if(recievedMessage != null) {
			JSONObject response = new JSONArray(recievedMessage).getJSONObject(0);
			if (!response.getBoolean("successful")) {
				throw new GroupMeException("Push API failed: \"" + response.getString("error") + "\"", 500);
			}
		}else {
			throw new GroupMeException("Could not connect to server", 500);
		}
		recievedMessage = null;
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
	public void onClose() {
		webSocket = null;
	}

	@Override
	public void onError(IOException e) {
		e.printStackTrace();
	}

	@Override
	public void onMessage(WebSocketMessage msg) {
		recievedMessage = msg.getText();
	}
	
	public void waitForMessage(int milliTimeout, int checkTime) {
		int timeAccumulated = 0;
		
		while(recievedMessage == null && timeAccumulated < milliTimeout) {
			timeAccumulated += checkTime;
			try {
				Thread.sleep(checkTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
				recievedMessage = null;
				return;
			}
		}
		System.out.printf("Waited %.3f seconds\n", timeAccumulated/1000f);
	}
	
	public void waitForMessage(int milliTimeout) {
		this.waitForMessage(milliTimeout, 10);
	}

	@Override
	public void onOpen() {}

	@Override
	public void onPing() {}

	@Override
	public void onPong() {}
	
	public void closeWebSocket() {
		if(webSocket != null) {
			webSocket.close();
		}
	}

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
