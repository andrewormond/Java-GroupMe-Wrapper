package github.adeo88.groupme.api.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

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
				printArray(Group.index(api));
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
