package github.adeo88.groupme.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import de.roderick.weberknecht.WebSocket;
import de.roderick.weberknecht.WebSocketEventHandler;
import de.roderick.weberknecht.WebSocketMessage;
import github.adeo88.groupme.api.events.MessageEvent;
import github.adeo88.groupme.api.events.PushEventListener;

public class GroupMePushAPI implements WebSocketEventHandler {
	public String clientID;
	public final String token; // TODO: Hide access to token
	public final String user_id;

	private int pushID = 0;
	private String recievedMessage = null;
	private PushEventListener listener;

	private WebSocket webSocket;
	
	

	public GroupMePushAPI(String token, String user_id) {
		super();
		this.token = token;
		this.user_id = user_id;
	}

	public void setPushEventListener(PushEventListener listener) {
		this.listener = listener;
	}

	public void pushApiHandshake(PushEventListener listener) throws GroupMeException {
		this.setPushEventListener(listener);
		URI url = null;
		try {
			url = new URI("wss://push.groupme.com/faye");
		} catch (URISyntaxException e) {
			throw new GroupMeException(e.getMessage(), 500);
		}
		webSocket = new WebSocket(url);
		// webSocket.debug = true;

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

		if (recievedMessage != null) {
			JSONObject response = new JSONArray(recievedMessage).getJSONObject(0);
			this.clientID = response.getString("clientId");
			System.out.printf("ClientId: %s\n", this.clientID);
			if (!response.getBoolean("successful")) {
				throw new GroupMeException("Push API Handshake failed", 500);
			}
		} else {
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
		payload.put("subscription", "/user/" + user_id);
		payload.put("id", ++this.pushID);

		JSONObject ext = new JSONObject();
		ext.put("access_token", this.token);
		ext.put("timestamp", new Date().getTime());
		payload.put("ext", ext);

		JSONArray body = new JSONArray();
		body.put(payload);

		webSocket.send(body.toString());

		waitForMessage(5000, 5);

		if (recievedMessage != null) {
			JSONObject response = new JSONArray(recievedMessage).getJSONObject(0);
			if (!response.getBoolean("successful")) {
				throw new GroupMeException("Push API failed: \"" + response.getString("error") + "\"", 500);
			}
		} else {
			throw new GroupMeException("Could not connect to server", 500);
		}
		recievedMessage = null;
	}

	public void pollData() throws GroupMeException {
		if (clientID == null || this.webSocket == null) {
			throw new GroupMeException("No clientId found. pushApiHandshake must successfully complete first.", 400);
		}

		JSONObject payload = new JSONObject();
		payload.put("channel", "/meta/connect");
		payload.put("clientId", this.clientID);
		payload.put("connectionType", "long-polling");
		payload.put("id", ++this.pushID);
		JSONArray body = new JSONArray();
		body.put(payload);

		this.webSocket.send(body.toString());

		/*
		 * JSONArray response =
		 * Utils.sendPostRequestToArray("https://push.groupme.com/faye", null,
		 * body.toString()); for (int i = 0; i < response.length(); i++) { JSONObject
		 * data = response.getJSONObject(i); ChannelListener listener =
		 * this.channels.get(data.getString("channel")); if (listener != null) {
		 * listener.onChannelData(data);
		 * 
		 * } else { System.out.printf("Unkown: [%s] : ", data.getString("channel"));
		 * System.out.println(data); } }
		 */
	}

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
		if (this.recievedMessage.startsWith("[{") && this.recievedMessage.endsWith("}]")) {
			JSONArray array = new JSONArray(this.recievedMessage);
			for (int i = 0; i < array.length(); i++) {
				JSONObject channelMsg = array.getJSONObject(i);
				if (channelMsg.has("data")) {
					// Update
					JSONObject data = channelMsg.getJSONObject("data");
					String type = data.getString("type");
					if (type.equals("line.create")) {
						MessageEvent event = new MessageEvent(data);
						listener.onMessage(event);
					} else {
						System.out.printf("[%d | %s] %s : %s\n", i, type, channelMsg.getString("channel"),
								data.toString());
					}
				} else {
					// System
					System.out.printf("[%d | SYSTEM]  : %s\n", i, channelMsg.getString("channel"));

				}
			}
		} else {
			System.out.printf("Recieved: %s\n", this.recievedMessage);

		}
	}

	public void waitForMessage(int milliTimeout, int checkTime) {
		int timeAccumulated = 0;

		while (recievedMessage == null && timeAccumulated < milliTimeout) {
			timeAccumulated += checkTime;
			try {
				Thread.sleep(checkTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
				recievedMessage = null;
				return;
			}
		}
	}

	public void waitForMessage(int milliTimeout) {
		this.waitForMessage(milliTimeout, 10);
	}

	@Override
	public void onOpen() {
	}

	@Override
	public void onPing() {
		System.out.println("ping");
	}

	@Override
	public void onPong() {
		System.out.println("pong");
	}

	public void closeWebSocket() {
		if (webSocket != null) {
			webSocket.close();
		}
	}
}
