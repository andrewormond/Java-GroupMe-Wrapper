package github.adeo88.groupme.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

public class GroupMeAPI {

	private String token;
	private final String USER_AGENT = "Mozilla/5.0";
	public boolean debugEnabled = false;

	public void println(Object obj) {
		if (this.debugEnabled) {
			System.out.println(obj);
		}
	}

	public void printEx(Exception e) {
		if (this.debugEnabled) {
			e.printStackTrace();
		}
	}

	public void printSep(String title, PrintStream ps) {
		if (this.debugEnabled) {
			int n = 100 - title.length();
			for (int i = 0; i < n / 2; i++) {
				ps.print('-');
			}

			ps.print(title);

			for (int i = 0; i < n / 2; i++) {
				ps.print('-');
			}
			ps.println();
		}
	}

	public void printSep(PrintStream ps) {
		printSep("", ps);
	}

	public void setToken(String token) {
		this.token = token;
		println("Set token to: " + token);
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

	public int enableSMS(int hours_enabled, boolean suppress) throws GroupMeException {
		JSONObject payload = new JSONObject();
		payload.put("duration", hours_enabled);
		if (suppress) {
			payload.put("registration_id", token);
		}
		return Utils.responseToCode(this.sendPostRequest("/users/sms_mode", payload.toString(), true));
	}

	public int disableSMS() throws GroupMeException {
		return Utils.responseToCode(this.sendPostRequest("/users/sms_mode/delete", "", true));
	}

}
