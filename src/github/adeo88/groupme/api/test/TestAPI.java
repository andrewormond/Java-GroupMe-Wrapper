package github.adeo88.groupme.api.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

import org.json.JSONObject;

import github.adeo88.groupme.api.Group;
import github.adeo88.groupme.api.GroupMeAPI;
import github.adeo88.groupme.api.GroupMeException;
import github.adeo88.groupme.api.Member;
import github.adeo88.groupme.api.User;

public class TestAPI {

	public static int DIVL = 60;

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
		if (objects.length > 0) {
			className = objects[0].getClass().getSimpleName();
		} else {
			className = objects.getClass().getSimpleName();
		}
		printSep(String.format("%s [%d]", className, objects.length), System.out);
		for (int i = 0; i < objects.length; i++) {
			System.out.println(objects[i]);
		}
		printSep(System.out);
	}

	private TestAPI() {
	}

	public static void main(String[] args) {
		System.out.println("Starting API Test");
		GroupMeAPI api;
		try {

			api = new GroupMeAPI(loadKey("token.txt"));
			api.debugEnabled = true;

			try {
				String groupID = "41685931";
				String userID = "55871106";
				Group group = Group.show(groupID, api);
				System.out.println(group);
				printArray(group.members);
				System.out.println("Remove: " + group.removeMember(userID, api));
				Thread.sleep(250);
				String result_id = group.addMember("Jerry", userID, api);
				System.out.println("resultID: " + result_id);

				int i = 1;
				Member[] results = null;
				do {
					try {
						System.out.println("Result Attempt #" + i++);
						Thread.sleep(100);
						results = group.getResults(result_id, api);
					} catch (GroupMeException e) {

					}
				} while (results == null);

				TestAPI.printArray(results);
				// printArray(group.indexMessages(api));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		System.out.println("Ending API Test");
	}

}
