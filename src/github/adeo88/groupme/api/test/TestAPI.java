package github.adeo88.groupme.api.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

import org.json.JSONObject;

import de.roderick.weberknecht.WebSocket;
import de.roderick.weberknecht.WebSocketEventHandler;
import de.roderick.weberknecht.WebSocketMessage;
import github.adeo88.groupme.api.Group;
import github.adeo88.groupme.api.GroupMeAPI;
import github.adeo88.groupme.api.GroupMeException;
import github.adeo88.groupme.api.GroupMePushAPI;
import github.adeo88.groupme.api.User;
import github.adeo88.groupme.api.events.MessageEvent;
import github.adeo88.groupme.api.events.PushEventListener;

public class TestAPI {

	public static int DIVL = 100;

	public static String loadKey(String filename) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(filename));
		String key = in.readLine().trim();
		in.close();
		return key;

	}

	public static void printSep(String title, PrintStream ps) {
		int n = DIVL - title.length();
		for (int i = 0; i < n / 2; i++) {
			ps.print('-');
		}

		ps.print(title);

		for (int i = 0; i < n / 2; i++) {
			ps.print('-');
		}
		ps.println();
	}

	public static void printSep(PrintStream ps) {
		printSep("", ps);
	}

	public static void printArray(Object[] objects) {
		String className;
		if (objects != null && objects.length > 0) {
			className = objects[0].getClass().getSimpleName();
		} else if (objects == null) {
			return;
		} else {
			className = objects.getClass().getSimpleName();
		}
		printSep(String.format("%s [%d]", className, objects.length), System.out);
		for (int i = 0; i < objects.length; i++) {
			System.out.println(objects[i]);
		}
		printSep(System.out);
	}

	public static void dumpJSON(int indent, JSONObject obj) {
		String indentString = "";
		for (int i = 0; i < indent; i++) {
			indentString += "\t";
		}
		System.out.println(indentString + "{");
		for (String key : obj.keySet()) {
			if (obj.optJSONObject(key) != null) {
				System.out.print(indentString + key + " : ");
				dumpJSON(indent + 1, obj.getJSONObject(key));
			} else {
				System.out.println(indentString + key + " : " + obj.get(key).toString());
			}
		}
		System.out.println(indentString + "}");
	}

	public static void dumpJSON(JSONObject obj) {
		dumpJSON(0, obj);
	}

	private TestAPI() {
	}

	public static void main(String[] args) {
		printSep("Starting API Test", System.out);
		System.out.println();
		final String authURL = "https://oauth.groupme.com/oauth/authorize?client_id=wQu3v27Sf7EKKTvfXdP1kjZ0yDBX97UGuZ2QGHJ2ukBpSx0S";

		try {
			GroupMeAPI api = new GroupMeAPI(loadKey("token.txt"));
			String groupId = "41685931";
			Group group = Group.show(groupId, api);
			GroupMePushAPI push = new GroupMePushAPI(api.token, User.Me(api).user_id);
			push.pushApiHandshake(new PushEventListener() {

				@Override
				public void onMessage(MessageEvent event) {
					System.out.printf("onMessage: %s\n", event.toString());
				}

			});

			push.pushUserSubscribe();
			push.pollData();
			Thread.sleep(1000);
			group.createMessage("Test: " + new Random().nextInt(), "" + new Random().nextLong(), null, api);
			Thread.sleep(60 * 1000);
			push.closeWebSocket();
		} catch (GroupMeException | IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println();
		printSep("Ending API Test", System.out);
	}

}
