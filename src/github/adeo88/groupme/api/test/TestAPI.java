package github.adeo88.groupme.api.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import github.adeo88.groupme.api.Block;
import github.adeo88.groupme.api.Bot;
import github.adeo88.groupme.api.DirectMessage;
import github.adeo88.groupme.api.Group;
import github.adeo88.groupme.api.Group.TimePeriod;
import github.adeo88.groupme.api.GroupMeAPI;
import github.adeo88.groupme.api.GroupMeException;
import github.adeo88.groupme.api.Member;
import github.adeo88.groupme.api.Message;
import github.adeo88.groupme.api.User;
import github.adeo88.groupme.api.Utils;
import github.adeo88.groupme.bots.BotManager;

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
	

	public static void dumpJSON(JSONObject obj) {
		System.out.println("{");
		for(String key : obj.keySet()) {
			if(obj.optJSONObject(key) != null) {
				System.out.print(key+" : ");
				dumpJSON(obj.getJSONObject(key));
			}else {
				System.out.println(key+" : "+obj.get(key).toString());
			}
		}
		System.out.println("}");
	}

	private TestAPI() {
	}

	public static void main(String[] args) {
		printSep("Starting API Test", System.out);
		System.out.println();

		GroupMeAPI api;
		try {

			api = new GroupMeAPI(loadKey("token.txt"));
			api.debugEnabled = true;

			try {
				String groupID = "41685931"; // Jerry test groupme
				printArray(Group.indexGroups(api, Optional.empty(), Optional.of(50), Optional.of(true)));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		System.out.println();
		printSep("Ending API Test", System.out);
	}

}
