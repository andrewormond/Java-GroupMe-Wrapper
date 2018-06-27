package github.adeo88.groupme.api.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

import github.adeo88.groupme.api.Group;
import github.adeo88.groupme.api.GroupMeAPI;
import github.adeo88.groupme.api.GroupMeException;

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
		printSep(className, System.out);
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
				Group group = Group.show(groupID, api);
				System.out.println(group);
				printArray(group.members);
				
				String userID = "55871106";
				/*
				System.out.println("Add "+userID+" result: "+group.addMember("Jerry", userID, api));
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Remove "+userID+" result: "+group.removeMember(userID, api));
				*/
				String name = "Andrew #"+new Random().nextInt(100);
				System.out.println("Update name result: "+group.updateNickname(name, api));
				
			} catch (GroupMeException e) {
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
