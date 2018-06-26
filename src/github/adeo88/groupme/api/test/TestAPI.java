package github.adeo88.groupme.api.test;

import github.adeo88.groupme.api.GroupMeAPI;

public class TestAPI {

	private TestAPI() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		System.out.println("Starting API Test");
		
		GroupMeAPI api = new GroupMeAPI("");

		System.out.println("Ending API Test");
	}

}
